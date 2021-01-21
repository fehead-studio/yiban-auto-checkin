package ink.verge.utils.checkin;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

/**
 * @Author Verge
 * @Date 2020/11/3 16:18
 * @Version 1.0
 */
public class CodeGenerator {
    public static void main(String[] args) {

        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();

        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        // 获取当前项目根路径
        String projectPath = System.getProperty("user.dir");
        gc.setOutputDir(projectPath+"/src/main/java");
        gc.setAuthor("Verge");
        gc.setOpen(false); //不打开生产的文件
        gc.setFileOverride(false); //不覆盖之前生成的文件
        //gc.setServiceName("%Service");
        gc.setIdType(IdType.AUTO);// 主键策略 自增  注意要和数据库中表实际情况对应
        gc.setDateType(DateType.ONLY_DATE);
        gc.setSwagger2(true);//自动开启swagger2的支持
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUrl("jdbc:mysql://47.93.200.138:3306/yiban_auto_checkin_test?useSSL=false&useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8");
        dsc.setUsername("root");
        dsc.setPassword("d5aDEHk2rORSQCxl");
        dsc.setDbType(DbType.MYSQL);
        mpg.setDataSource(dsc);

        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setParent("ink.verge.utils.checkin");
        pc.setController("controller");
        pc.setService("service");
        pc.setEntity("entity");
        pc.setMapper("mapper");
        mpg.setPackageInfo(pc);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        //可以用同配符号:表示生成t_开头的对应库下所有表
        strategy.setInclude("user");
        strategy.setNaming(NamingStrategy.underline_to_camel);// 下划线转驼峰
        //strategy.setTablePrefix("tb_");//去掉t_这个前缀后生成类名
        strategy.setEntityLombokModel(true);// 自动生成lombok注解  记住要有lombok依赖和对应的插件哈
        //strategy.setLogicDeleteFieldName("is_deleted");//设置逻辑删除字段 要和数据库中表对应哈

        // 设置创建时间和更新时间自动填充策略
        /*TableFill created_date = new TableFill("created_date", FieldFill.INSERT);
        TableFill updated_date = new TableFill("updated_date", FieldFill.INSERT_UPDATE);
        ArrayList<TableFill> tableFills = new ArrayList<>();
        tableFills.add(created_date);
        tableFills.add(updated_date);
        strategy.setTableFillList(tableFills);
*/
        // 乐观锁策略
        strategy.setVersionFieldName("version");
        strategy.setRestControllerStyle(true);//采用restful 风格的api
        strategy.setControllerMappingHyphenStyle(true); // controller 请求地址采用下划线代替驼峰
        mpg.setStrategy(strategy);

        // 执行
        mpg.execute();
    }
}
