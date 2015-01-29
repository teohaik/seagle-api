package gr.uom.se.util.manager;

import gr.uom.se.util.config.ConfigConstants;
import gr.uom.se.util.config.ConfigManager;
import gr.uom.se.util.module.ModuleConstants;
import gr.uom.se.util.module.ModuleManager;
import gr.uom.se.util.module.annotations.Property;
import gr.uom.se.util.module.annotations.ProvideModule;

public class Bean {
   
      @Property(domain = ManagerConstants.DEFAULT_DOMAIN, name = ModuleConstants.DEFAULT_MODULE_MANAGER_PROPERTY)
      ModuleManager moduleManager;
      
      @Property(domain = ManagerConstants.DEFAULT_DOMAIN, name = ManagerConstants.DEFAULT_MANAGER_PROPERTY)
      AbstractManager mainManager;
      
      @Property(domain = ManagerConstants.DEFAULT_DOMAIN, name = ConfigConstants.DEFAULT_CONFIG_MANAGER_PROPERTY)
      ConfigManager configManager;

      @ProvideModule
      public Bean(ModuleManager moduleManager, AbstractManager mainManager,
            ConfigManager configManager) {
         this.mainManager = mainManager;
         this.moduleManager = moduleManager;
         this.configManager = configManager;
      }
   }