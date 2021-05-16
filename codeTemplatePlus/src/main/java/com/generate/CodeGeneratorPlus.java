package com.generate;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author liujianghua
 * @version 1.0
 * @date 2020/11/28 22:10
 * @description 标题
 * @package
 */
public class CodeGeneratorPlus {
    static Properties properties = null;

    static {
        InputStream resourceAsStream = CodeGeneratorPlus.class.getClassLoader().getResourceAsStream("application.properties");
        System.out.println(resourceAsStream);
        if (properties == null) {
            properties = new Properties();
        }
        try {
            properties.load(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void main(String[] args) {

        //是否开启系统工程路径获取 默认是true
        String property = properties.getProperty("steven.enableProject");
        //获取到系统工程路径值
        String projectPath=properties.getProperty("steven.projectPath");
        //实体属性 Swagger2 注解是否开启
        String swaggerFlag = properties.getProperty("steven.swagger");
        String url = properties.getProperty("steven.url");
        String driver = properties.getProperty("steven.driver");
        String username = properties.getProperty("steven.username");
        String password = properties.getProperty("steven.password");

        //设置表前缀
        String prefixFlagBoolean = properties.getProperty("steven.prefixFlag");
        String prefixTable = properties.getProperty("steven.prefix");

        //模块名称
        String moduleName = properties.getProperty("steven.moduleName");
        //父路径 两层目录
        String parentPath = properties.getProperty("steven.parent");

        //是否集成corecontroller
        String superControllerFlag = properties.getProperty("steven.superControllerFlag");

        //继承的coreController的路径
        String superControllerPath = properties.getProperty("steven.superController");

        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();
        // 全局配置
        GlobalConfig gc = new GlobalConfig();

        if(!Boolean.valueOf(property).booleanValue()){
            //获取默认的路径
            projectPath = System.getProperty("user.dir");
        }
        gc.setOutputDir(projectPath + "/src/main/java");
        gc.setAuthor("ljh");
        gc.setOpen(false);
        gc.setServiceName("%sService");

        // heima.swagger
        gc.setSwagger2(Boolean.valueOf(swaggerFlag));

        mpg.setGlobalConfig(gc);

        // 数据源配置 heima.url
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl(url);
        //dsc.setSchemaName("public");
        dsc.setDriverName(driver);
        dsc.setUsername(username);
        dsc.setPassword(password);
        dsc.setTypeConvert(new MySqlTypeConvert() {
            @Override
            public DbColumnType processTypeConvert(GlobalConfig globalConfig, String fieldType) {
                //tinyint转换成Boolean
                if (fieldType.toLowerCase().contains("tinyint")) {
                    return DbColumnType.INTEGER;
                }
                // 这个暂时不需要将数据库中datetime转换成date
                /*if (fieldType.toLowerCase().contains("datetime")) {
                    return DbColumnType.DATE;
                }*/
                return (DbColumnType) super.processTypeConvert(globalConfig, fieldType);
            }

        });


        mpg.setDataSource(dsc);

        // 包配置
        PackageConfig pc = new PackageConfig();
        //模块名称
        pc.setModuleName(moduleName);
        //设置parent 不能设置为空 todo
        pc.setParent(parentPath);

        //设置包名为pojo
        pc.setEntity("pojo");

        mpg.setPackageInfo(pc);


        // 自定义配置
        // https://baomidou.com/guide/generator.html#%E8%87%AA%E5%AE%9A%E4%B9%89%E4%BB%A3%E7%A0%81%E6%A8%A1%E6%9D%BF
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {

            }

            @Override
            public void initTableMap(TableInfo tableInfo) {
                //设置核心feign所在的包路径
                String coreFeignPath = properties.getProperty("steven.superFeign");
                String superFeignFlag = properties.getProperty("steven.superFeignFlag");
                String feignApplicationName = properties.getProperty("steven.application.name");

                //feign的设置
                Map<String,Object> map = new HashMap<String,Object>();
                //设置包名 #feign
                map.put("feignPackage",pc.getParent()+".feign");

                if(Boolean.valueOf(superFeignFlag)){
                    map.put("coreFeignPath",coreFeignPath);
                }
                //设置feignclient 设置feignclient名称
                map.put("feignApplicationName",feignApplicationName);
                //设置核心接口类名
                map.put("coreFeignClassName",coreFeignPath.substring(coreFeignPath.lastIndexOf(".")+1));

                //设置parent包名 指定给application模板来使用
                map.put("applicationPackage",parentPath);



                this.setMap(map);
            }
        };

        // 如果模板引擎是 freemarker
        // 生成mapper.xml文件的模板
        String templatePath = "/templates/mapper.xml.ftl";

        // 如果模板引擎是 velocity
        // String templatePath = "/templates/mapper.xml.vm";

        // 自定义输出配置
        List<FileOutConfig> focList = new ArrayList<>();
        // 自定义配置会被优先输出
        String finalProjectPath = projectPath;
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                return finalProjectPath
                        + "/src/main/resources/mapper/"
                        //+ pc.getModuleName()
                        + "/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
            }
        });

        //自定义feign模板生成
        String feignTemplateFilePath="/templates/feign.java.ftl";

        focList.add(new FileOutConfig(feignTemplateFilePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                String parent = pc.getParent();
                String[] split = parent.split("\\.");

                //生成目录和类  #feign
                return finalProjectPath+"/src/main/java/"
                        +split[0]+"/"+split[1]+"/"+split[2]+"/feign/"+tableInfo.getEntityName()+"Feign"+StringPool.DOT_JAVA;
            }
        });


        //自定义javaApplication模板生成
        String appplicationTemplateFilePath="/templates/application.java.ftl";

        focList.add(new FileOutConfig(appplicationTemplateFilePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                String parent = pc.getParent();
                String[] split = parent.split("\\.");

                //生成目录和类  #feign
                String str = pc.getModuleName();
                String s = str.substring(0, 1).toUpperCase() + str.substring(1);
                return finalProjectPath+"/src/main/java/"
                        +split[0]+"/"+split[1]+"/"+s+"Application"+StringPool.DOT_JAVA;
            }
        });

        /*
        cfg.setFileCreate(new IFileCreate() {
            @Override
            public boolean isCreate(ConfigBuilder configBuilder, FileType fileType, String filePath) {
                // 判断自定义文件夹是否需要创建
                checkDir("调用默认方法创建的目录，自定义目录用");
                if (fileType == FileType.MAPPER) {
                    // 已经生成 mapper 文件判断存在，不想重新生成返回 false
                    return !new File(filePath).exists();
                }
                // 允许生成模板文件
                return true;
            }
        });
        */

        //cfg.setConfig();

        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);

        // 配置自定义模板为freemarker 默认使用velocity
        TemplateConfig templateConfig = new TemplateConfig();

        // 配置自定义输出模板
        //指定自定义模板路径，注意不要带上.ftl/.vm, 会根据使用的模板引擎自动识别
        templateConfig.setEntity("/templates/entity2.java");
        templateConfig.setController("/templates/controller2.java");
        //设置XML为null
        templateConfig.setXml(null);

        mpg.setTemplate(templateConfig);
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();

        //设置非序列化
        strategy.setEntitySerialVersionUID(false);
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        //生成注解字段
        strategy.setEntityTableFieldAnnotationEnable(true);
        //strategy.setSuperEntityClass("你自己的父类实体,没有就不用设置!");
        strategy.setEntityLombokModel(true);
        strategy.setRestControllerStyle(true);
        // 设置controller的父类全路径
        if(Boolean.valueOf(superControllerFlag)) {
            strategy.setSuperControllerClass(superControllerPath);
        }
        // 写于父类中的公共字段
        //strategy.setSuperEntityColumns("id");
        //strategy.setInclude(scanner("表名，多个英文逗号分割").split(","));
        strategy.setExclude("undo_log");
        //controller驼峰转换不设置转换
        strategy.setControllerMappingHyphenStyle(false);

        //设置表前缀
        if(Boolean.valueOf(prefixFlagBoolean)){
            strategy.setTablePrefix(prefixTable);
        }
        mpg.setStrategy(strategy);
        mpg.execute();
    }
}
