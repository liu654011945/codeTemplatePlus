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
 * @package com.itheima
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
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();
        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        String projectPath="";

        String property = properties.getProperty("heima.enableProject");
        if(Boolean.valueOf(property).booleanValue()){
            projectPath=properties.getProperty("heima.projectPath");
        }else {
            //获取默认的路径
            projectPath = System.getProperty("user.dir");
        }
        gc.setOutputDir(projectPath + "/src/main/java");
        gc.setAuthor("ljh");
        gc.setOpen(false);
        gc.setServiceName("%sService");

        // heima.swagger
        gc.setSwagger2(Boolean.valueOf(properties.getProperty("heima.swagger"))); //实体属性 Swagger2 注解

        mpg.setGlobalConfig(gc);

        // 数据源配置 heima.url
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl(properties.getProperty("heima.url"));
        // dsc.setSchemaName("public");
        dsc.setDriverName(properties.getProperty("heima.driver"));
        dsc.setUsername(properties.getProperty("heima.username"));
        dsc.setPassword(properties.getProperty("heima.password"));
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
        pc.setModuleName(properties.getProperty("heima.moduleName"));
        //设置parent 不能设置为空 todo
        pc.setParent(properties.getProperty("heima.parent"));
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
                //feign的设置
                Map<String,Object> map = new HashMap<String,Object>();
                //设置包名 #feign
                map.put("feignPackage",pc.getParent()+".feign");
                //设置核心feign所在的包路径
                String coreFeignPath = properties.getProperty("heima.superFeign");
                String superFeignFlag = properties.getProperty("heima.superFeignFlag");
                String feignApplicationName = properties.getProperty("heima.application.name");
                if(Boolean.valueOf(superFeignFlag)){
                    map.put("coreFeignPath",coreFeignPath);
                }
                //设置feignclient 设置feignclient名称
                map.put("feignApplicationName",feignApplicationName);
                //设置核心接口类名
                map.put("coreFeignClassName",coreFeignPath.substring(coreFeignPath.lastIndexOf(".")+1));



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
        if(Boolean.valueOf(properties.getProperty("heima.superControllerFlag"))) {
            strategy.setSuperControllerClass(properties.getProperty("heima.superController"));
        }
        // 写于父类中的公共字段
        //strategy.setSuperEntityColumns("id");
        //strategy.setInclude(scanner("表名，多个英文逗号分割").split(","));
        strategy.setExclude("undo_log");
        //controller驼峰转换不设置转换
        strategy.setControllerMappingHyphenStyle(false);

        //设置表前缀
        strategy.setTablePrefix("tb_");
        mpg.setStrategy(strategy);
        mpg.execute();
    }
}
