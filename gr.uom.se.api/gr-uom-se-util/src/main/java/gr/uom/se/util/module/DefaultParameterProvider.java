/**
 * 
 */
package gr.uom.se.util.module;

import gr.uom.se.util.mapper.Mapper;
import gr.uom.se.util.module.annotations.NULLVal;
import gr.uom.se.util.module.annotations.Property;
import gr.uom.se.util.property.DomainPropertyProvider;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * An implementation of parameter provider which is based on a configuration
 * manager, from where it can retrieve the values of required parameters.
 * <p>
 * Each parameter is resolved using the following algorithm:
 * <ol>
 * <li>If the parameter has a {@link Property} annotation:</li>
 * <ol>
 * <li>The configuration domain and the property name will be read from
 * annotation.</li>
 * <li>Based on these coordinates (domain, name) the configuration manager will
 * be queried for the parameter value. If found it will be returned.</li>
 * <li>If the value was not found on the previous step, the map provided when
 * the method
 * {@link #getParameter(Class, Annotation[], Map, ModulePropertyLocator)} is
 * called will be queried for the value. If found and the value is the same as
 * type as the parameter then it will be returned. If found and the value is not
 * the same type as the parameter, then its toString() method will be called and
 * the string representation of it will be converted to a value which is
 * compatible with the parameter.</li>
 * <li>If the value was not found in the previous step then the
 * {@code stringVal} value of the annotation will be analyzed. If the string
 * value is equals to {@link NULLVal#NULL_STR} a null will be returned. If it is
 * equals to {@link NULLVal#LOAD_STR} the parameter will be considered a module
 * and it will be loaded by an instance of a {@link ModuleLoader}. If the string
 * value is none of the above it will be considered a string representation of
 * the parameter and will be converted to a value that is compatible with the
 * parameter.</li>
 * </ol>
 * <li>If the parameter is not annotated it will be considered a module and it
 * will be loaded using an instance of {@link ModuleLoader}.</li> </ol>
 * 
 * The strategy to use a module loader to load a parameter that is not annotated
 * or can not be resolved is as follows:
 * <ol>
 * <li>Look for the loader under domain
 * {@link ModuleConstants#getDefaultConfigFor(Class)} and property name
 * {@link ModuleConstants#LOADER_PROPERTY}, where Class is the type of
 * parameter. If the loader was found use it.</li>
 * <li>If the loader was not found in previous step, then look for it under
 * domain {@link ModuleConstants#DEFAULT_MODULE_CONFIG_DOMAIN} and property name
 * {@link ModuleConstants#DEFAULT_MODULE_LOADER_PROPERTY}. If found then use it.
 * </li>
 * <li>If the loader was not found in previous step, then check if a loader was
 * provided when the instance of this class was created. If not, then create an
 * instance of {@link DefaultModuleLoader} and use it. If yes, then use the
 * provided loader.</li>
 * </ol>
 * Each time this provider looks for a property it will look first in
 * configuration manager (provided when created, null or not) if the value is
 * not found there it will look under the provided map (if the map is not null).
 * 
 * @see DefaultModuleLoader
 * @author Elvis Ligu
 * @version 0.0.1
 * @since 0.0.1
 * @see Property
 * @see DefaultModuleLoader
 */
@Property(domain = ModuleConstants.DEFAULT_MODULE_CONFIG_DOMAIN, name = ModuleConstants.PARAMETER_PROVIDER_PROPERTY)
public class DefaultParameterProvider implements ParameterProvider {

   /**
    * Used to check for properties if we can load them from here.
    * <p>
    */
   private final DomainPropertyProvider config;

   /**
    * Used in case a parameter has no annotation, so we can not find it within
    * config, and we need to load it.
    * <p>
    * This loader is a cached loader for the default loader.
    */
   protected volatile ModuleLoader loader;

   /**
    * A locator to look up module's properties.
    */
   private final ModulePropertyLocator locator;

   /**
    * Create a parameter provider with the given config, and loader.
    * <p>
    * Both parameters can be null.
    */
   public DefaultParameterProvider(DomainPropertyProvider config,
         ModuleLoader loader, ModulePropertyLocator locator) {
      this.config = config;
      this.loader = loader;
      if (locator == null) {
         locator = new DefaultModulePropertyLocator();
      }
      this.locator = locator;
   }

   /**
    * Create a parameter provider with the given config, and loader.
    * <p>
    * Both parameters can be null.
    */
   public DefaultParameterProvider(DomainPropertyProvider config,
         ModuleLoader loader) {
      this(config, loader, null);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public <T> T getParameter(Class<T> parameterType, Annotation[] annotations,
         Map<String, Map<String, Object>> properties,
         ModulePropertyLocator propertyLocator) {

      // In case the others do not provide any locator, we should use the
      // default one
      if (propertyLocator == null) {
         propertyLocator = locator;
      }

      // If there are not annotations the loader will try to parse the
      // parameter metadata and to find info if it can be loaded
      // by this loader
      if (annotations == null || annotations.length == 0) {
         return resolveLoader(parameterType, properties, propertyLocator).load(
               parameterType, propertyLocator);
      }

      // We should check the number of @Property annotations
      // and will be skipping other non related annotations
      Property propertyAnnotation = ModuleUtils
            .getPropertyAnnotation(annotations);

      // In case this parameter has other annotations
      // rather than known ones
      if (propertyAnnotation == null) {
         return resolveLoader(parameterType, properties, propertyLocator).load(
               parameterType, propertyLocator);
      }

      // We have a @Property annotation
      // and we should extract info from there
      return extractValue(parameterType, propertyAnnotation, properties,
            propertyLocator);
   }

   protected ModuleLoader resolveLoader(Class<?> type,
         Map<String, Map<String, Object>> properties,
         ModulePropertyLocator propertyLocator) {
      ModuleLoader loader = propertyLocator.getLoader(type, config, properties);
      // If no loader was found then create a default module loader
      // if it is not created
      if (loader == null) {
         if (this.loader == null) {
            this.loader = new DefaultModuleLoader(config, this);
         }
         loader = this.loader;
      }
      return loader;
   }

   /**
    * Extract a value for the given parameter based on its annotation, and the
    * configuration.
    * <p>
    * If the value can not be extracted from the configuration then the default
    * stringval value will be converted to a value. The parameter should be a
    * primitive type.
    * 
    * @param parameterType
    * @param annotation
    * @param properties
    * @return
    */
   protected <T> T extractValue(Class<T> parameterType, Property annotation,
         Map<String, Map<String, Object>> properties,
         ModulePropertyLocator propertyLocator) {
      String domain = annotation.domain();
      String name = annotation.name();
      String strval = annotation.stringVal();
      return this.extractValue(parameterType, domain, name, strval, properties,
            propertyLocator);
   }

   @SuppressWarnings("unchecked")
   protected <T> T extractValue(Class<T> parameterType, String domain,
         String name, String strval,
         Map<String, Map<String, Object>> properties,
         ModulePropertyLocator propertyLocator) {

      Object pVal = propertyLocator.getProperty(domain, name, Object.class,
            config, properties);

      // If the value is found,
      // check if it is a compatible type with T, if so
      // return the value, otherwise consider the value as
      // a string which should be parsed and converted to
      // a value
      if (pVal != null) {
         if (parameterType.isAssignableFrom(pVal.getClass())) {
            return (T) pVal;
         } else {
            strval = pVal.toString();
         }
      }
      // In this case the string val should have a value
      // If the value is same as NULL_STR that means we
      // should return a null value, if it is LOAD_STR then
      // we should consider this property as a module that
      // should be loaded by a loader
      if (strval != null) {
         if (strval.equals(NULLVal.NULL_STR)) {
            return null;
         }
         if (strval.equals(NULLVal.LOAD_STR)) {
            // Pass null to annotations type so the provider will
            // know about it and will load the parameter instead of
            // passing it at this method again
            return getParameter(parameterType, null, properties,
                  propertyLocator);
         }
      }

      // Use a mapper to map the default string value to the given type.
      // This is the case when a string value has been specified as the
      // default value for the property in its annotation. That means we
      // should make a binding from string to the right type if we can.
      // To bind a string value to a type, usually the type should be
      // a primitive type or a primitive wrapper. However this can be
      // changed if we specify a mapper which can maps that values
      // accordingly.
      if (strval != null && !strval.isEmpty()) {

         Mapper mapper = propertyLocator.getMapperOfType(parameterType,
               String.class, parameterType, config, properties);

         if (mapper == null) {
            throw new IllegalArgumentException(
                  "Can not map a string value to a type: " + parameterType);
         }
         return mapper.map(strval, parameterType);
      }

      // The property could not be initialized so an exception is thrown
      throw new IllegalArgumentException("Can not extract a value of "
            + parameterType.getName() + " " + " at " + domain + ":" + name
            + " with value " + strval);
   }
}
