# tasks_completed.md �� ���������鵵

> **������**��2026-06-09T18:55
> **归档数**：643 条
> **�ĵ���λ**��ȫ����ʷ��ɼ�¼���������ݲ���

---

## �鵵��ʽ

ÿ����¼���������ֶΣ�

| �ֶ� | ˵�� |
|------|------|
| ������ | ĩ������������� |
| �������� | �����ĵ����� |
| ���ʱ�� | ISO 8601 ʱ��� |
| ״̬ | ? ��� / ?? ���� |
| ժҪ | һ�仰���������� |
| Git SHA | commit ��ϣ��ǰ8λ�� |
| ����ԭ�� | ������ʱ��д |

---

## �鵵��¼

> **��������**����ĳģ�������һ����ɼ�¼ʱ�����·�������ģ��Ķ������顣
> ��ʼ״̬Ϊ�հף���Ԥ�����κ�ģ�����

### P0-001 - �����Ŀ��ܴ

| ������ | �������� | ���ʱ�� | ״̬ | ժҪ | Git SHA |
|---------|---------|---------|:---:|------|:------:|
| P0-001-001-001-001-001 | ִ��Spring Initializr������Ŀ | 2026-05-28T20:30 | ? | ����Spring Boot 3.4.5��Ŀ�Ǽ�(pom.xml+����) | 5e64aee2 |
| P0-001-001-001-001-002 | ��֤��Ŀ������ | 2026-05-28T20:36 | ? | �ų�JPA�Զ�����,��Ŀ1.2�������ɹ� | 46276bc6 |
| P0-001-001-001-002-001 | ����������Ŀ¼ | 2026-05-28T20:32 | ? | ��com.erp�´���13����׼�Ӱ�(common/config/controller/service/mapper/entity/dto/vo/enums/util/annotation/aspect/exception) | e2556c65 |
| P0-001-001-001-002-002 | ����ģ���Ӱ� | 2026-05-28T20:58 | ? | ����common��7���Ӱ�(result/exception/constant/enums/entity/utils/config)+����module/engine/framework������+framework��4���Ӱ�(tenant/dataperm/softdelete/codegen) | d1cfe198 |
| P0-001-001-001-002-003 | ��֤���ṹ������ | 2026-05-28T21:12 | ? | ��֤13����׼����������,����ȫСд,mvn compile BUILD SUCCESS(2s) | ec642500 |
| P0-001-001-001-003-001 | ��д������main���� | 2026-05-28T21:22 | ? | ErpAiApplication����(@SpringBootApplication+@MapperScan+@EnableAspectJAutoProxy),JVM����-Xms512m -Xmx1024m,mvn clean compile BUILD SUCCESS | 008f881a |
| P0-001-001-001-003-002 | ������������ | 2026-05-28T21:35 | ? | ����banner.txt�������+����.mvn/jvm.config JVM����(-Xms512m -Xmx1024m),mvn compile BUILD SUCCESS | 1515bab0 |
| P0-001-001-002-001-001 | ���Ӻ����������� | 2026-05-28T21:37 | ? | pom.xml����sa-token 1.39.0/postgresql/hutool 5.8.34/knife4j 4.5.0,�汾���Լ��й���,mvn compile BUILD SUCCESS | b728242c |
| P0-001-001-002-001-002 | ��֤�������� | 2026-05-28T21:39 | ? | dependency:tree��֤7�����������汾��ȷ,dependency:analyze�޳�ͻ,BUILD SUCCESS | (��֤����) |
| P0-001-001-002-002-001 | ���ӿ��������������� | 2026-05-28T21:41 | ? | ����devtools/configuration-processor/mapstruct 1.5.5.Final,����maven-compiler-pluginע�⴦����·��(lombok+mapstruct),BUILD SUCCESS | 1effd1eb |
| P0-001-001-002-002-002 | ���ÿ������߲��� | 2026-05-28T21:43 | ? | ����lombok.config��Ŀ������,application.yml����DevTools����,mvn compile�޾����޴��� | f2b7f238 |
| P0-001-001-002-002-003 | ��֤���߿��� | 2026-05-28T21:45 | ? | dependency:tree��֤devtools/mapstruct/lombok/configuration-processor�汾��ȷ,�޳�ͻ,BUILD SUCCESS | (��֤����) |
| P0-001-001-002-003-001 | ����dependencyManagement���� | 2026-05-28T21:47 | ? | ����dependencyManagementͳһ�汾����,�汾����(hutool 5.8.26/knife4j 4.3.0),����easyexcel 3.3.3,BUILD SUCCESS | 3252d919 |
| P0-001-001-002-003-002 | ͳһ�������汾�� | 2026-05-28T21:49 | ? | ��֤properties+dependencyManagement�汾��ȫ����ȷ(3.5.5/1.39.0/5.8.26/4.3.0/3.3.3/1.5.5.Final),BUILD SUCCESS | (��֤����) |
| P0-001-001-002-003-003 | ��֤������ͻ | 2026-05-28T21:51 | ? | dependency:analyze�޳�ͻ,tree -Dverbose��omitted,BUILD SUCCESS | (��֤����) |
| P0-001-001-003-001-001 | ��дdev�������� | 2026-05-28T21:53 | ? | ����application-dev.yml(server.port=8080/PostgreSQL/Redis/MyBatis-Plus/��־),����dev profile,BUILD SUCCESS | 84df9c8b |
| P0-001-001-003-001-002 | ��֤������Ч | 2026-05-28T22:01 | ? | mvn compile BUILD SUCCESS+Spring Boot 1.89�������ɹ�,dev profile����,YAML 6���������ȫ����ȷ����,��Ӳ����������Ϣ | 335c0ff4 |
| P0-001-001-003-001-003 | ���ð�ȫ���� | 2026-05-28T22:15 | ? | �޸�application-dev.yml��ȫ��:DB/Redis������û�������ע��,id-type��Ϊassign_id,logic-delete-field��Ϊis_deleted(boolean),mvn compileͨ�� | 2c76d96c |
| P0-001-001-003-001-004-001 | ��дtest�������� | 2026-05-28T22:20 | ? | ����application-test.yml(server.port=8081/PostgreSQL test-db/Redis test-redis/MyBatis-Plus/�ر�Swagger/��־INFO����),�������û�������ע��,mvn compileͨ�� | 090b379c |
| P0-001-001-003-001-004-002 | ��֤test�������� | 2026-05-28T22:39 | ? | test profile������֤:port=8081����ȷ,1.68������,5������ֵȫ����ȷ����,��Ӳ����������Ϣ,mvn compileͨ�� | (��֤����) |
| P0-001-001-003-001-005-001 | ��дstaging�������� | 2026-05-28T22:57 | ? | ����application-staging.yml(server.port=8082/${DB_URL}/${REDIS_HOST}/NoLoggingImpl/��־INFO),��������ȫ����������ע��,mvn compileͨ�� | c0f63ca0 |
| P0-001-001-003-001-005-002 | ��֤staging�������� | 2026-05-28T23:05 | ? | staging profile������֤:port=8082����ȷ,5������ֵȫ����ȷ����,DB_URL/REDIS_HOST��������ע��,��Ӳ����������Ϣ,mvn compileͨ�� | (��֤����) |
| P0-001-001-003-002-001 | ��дprod�������� | 2026-05-29T00:15 | ? | ����application-prod.yml(server.port=8080/${DB_PASSWORD}/${REDIS_PASSWORD}/${JWT_SECRET}/NoLoggingImpl/��־WARN/Swagger�ر�/HikariCP�������ӳ�),mvn compileͨ�� | 8d73fa3c |
| P0-001-001-003-002-002 | ��֤������Ч | 2026-05-29T10:56 | ? | ProdConfigValidationTest 10�����ȫ��PASS(server.port=8080/��������ע��/WARN��־/��Ӳ����/Swagger�ر�/NoLoggingImpl),mvn compile+test BUILD SUCCESS | 7a8f5b9f |
| P0-001-001-003-002-003 | ���ð�ȫ�ӹ� | 2026-05-29T11:04 | ? | application-prod.yml��ȫ�ӹ���֤:server.port=8080/��������ȫ��${DB_PASSWORD}/${REDIS_PASSWORD}/${JWT_SECRET}��������ע��/logging.com.erp=WARN/Swagger�ر�/NoLoggingImpl,ProdConfigValidationTest 10/10 PASS,mvn compile+test BUILD SUCCESS | f1fb3150 |
| P0-001-001-003-003-001-001 | ����MyBatis-Plusȫ���� | 2026-05-29T11:15 | ? | application.yml����MyBatis-Plusȫ������(id-type=ASSIGN_ID/logic-delete-field=isDeleted/logic-delete-value=1/map-underscore-to-camel-case=true/cache-enabled=false),mvn compile BUILD SUCCESS | e9bbd1c2 |
| P0-001-001-003-003-001-002 | ����Sa-Tokenȫ���� | 2026-05-29T12:00 | ? | application.yml����Sa-Tokenȫ������(token-name=satoken/timeout=2592000/active-timeout=1800/is-concurrent=true/is-share=true/token-style=uuid/is-log=false),mvn compile BUILD SUCCESS | 7cf63892 |
| P0-001-001-003-003-001-003 | �����ļ��ϴ���������ҳĬ��ֵ | 2026-05-29T12:25 | ? | application.yml����spring.servlet.multipart(max-file-size=10MB/max-request-size=100MB)+file.upload(allowed-types/path)+page(default-size=20/max-size=100),mvn compile BUILD SUCCESS | 9fe5387c |
| P0-001-001-003-003-002 | ��ȡ�������õ�application.yml | 2026-05-29T12:30 | ? | application.yml��ȡ5�������:profiles.active=${SPRING_PROFILES_ACTIVE:dev}/application.name=erp-ai/server.servlet.context-path=/api/jackson.date-format=yyyy-MM-dd HH:mm:ss/jackson.time-zone=GMT+8,����4��profile�ļ���ͻcontext-path,mvn compile BUILD SUCCESS | 5658bc98 |
| P0-001-001-003-003-003 | ��֤���ü̳���ȷ | 2026-05-29T12:45 | ? | �޸�4�����ò�һ��(logic-delete-field/value/id-type��Сд)+����profiles����MyBatis-Plus����+����actuator����+ͨ��/actuator/env��֤11�����ü̳���ȷ+������Ϣ��Ӳ����,mvn compile BUILD SUCCESS | 932913fa |
| P0-001-001-004-001-001 | ��дlogback-spring.xml | 2026-05-29T12:55 | ? | ����logback-spring.xml(CONSOLE+FILE+ERROR_FILE��Appender/��־��ʽ%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n/100MB+30�����+10GB����/springProfile dev=DEBUG prod=INFO test,staging=INFO),mvn compile BUILD SUCCESS | 604f5495 |
| P0-001-001-004-001-002 | ������־��ʽ����� | 2026-05-29T13:05 | ? | ��֤logback-spring.xml����������:CONSOLE+FILE˫Appender+��־��ʽ%d{yyyy-MM-dd HH:mm:ss.SSS}+100MB/30�����+springProfile(dev=DEBUG/prod=INFO)ȫ������Ҫ��,mvn compile BUILD SUCCESS | 604f5495 |
| P0-001-001-004-001-003 | ��֤��־��� | 2026-05-29T13:15 | ? | ��֤logback-spring.xml�������ձ�׼ȫ��ͨ��:��־��ʽ%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36}��ȷ/100MB+30�����������Ч/springProfile dev=DEBUG prod=INFO������ȷ,mvn compile+Logback 1.5.18�������� | (��֤����) |
| P0-001-001-004-002-001 | ����springProfile��־���� | 2026-05-29T14:00 | ? | logback-spring.xml����5��springProperty��̬��ȡlogging.level+����com.erp/com.baomidou.mybatisplus/org.springframework.web/org.hibernate.SQL/io.undertow��־����+4��application-*.ymlͬ������logging.level,mvn compile BUILD SUCCESS | 301ffaae |
| P0-001-001-004-002-002 | ���ö�̬��־���� | 2026-05-29T13:40 | ? | ��֤logback-spring.xml��̬��־��������������:5��springProperty��application.yml��ȡ(com.erp=DEBUG/INFO,com.baomidou.mybatisplus=WARN,org.springframework.web=INFO,org.hibernate.SQL=WARN,io.undertow=WARN)+application-dev.yml/prod.yml/test.yml/staging.yml��־����������ȷ,mvn compile BUILD SUCCESS | (��֤����) |
| P0-001-001-004-002-003 | ��֤��־������Ч | 2026-05-29T13:47 | ? | ���׶�����ȫ��PASS:V1��־��ʽ%d{yyyy-MM-dd HH:mm:ss.SSS}��ȷ/V2��������maxFileSize=100MB+maxHistory=30��/V3 springProfile dev+prod+test,staging������/V4 5��springProperty��̬ע��Դ+Logback 1.5.18 JoranConfigurator.doConfigure����ʱ�������쳣+application-dev.yml com.erp=DEBUG/application-prod.yml com.erp=INFO��̬������ȷ,mvn compile BUILD SUCCESS | 0fc6c98e |
| P0-001-001-005-001-001 | ����@Configuration������@Beanע�᷽ʽ | 2026-05-29T14:02 | ? | ����MybatisPlusConfig.java:@Configuration+@Bean MybatisPlusInterceptor(��TenantLineInnerInterceptor���⻧+��PaginationInnerInterceptor��ҳoverflow=true+��OptimisticLockerInnerInterceptor�ֹ���)+@Bean MetaObjectHandler�Զ����(createdAt/updatedAt/createdBy/updatedBy/version),mvn compile BUILD SUCCESS | 67802ae2 |
| P0-001-001-005-001-002 | �������԰� | 2026-05-29T14:30 | ? | ��֤MybatisPlusConfig.java�������԰�����:@Configuration+@Bean MybatisPlusInterceptor(TenantLine+Pagination overflow=true+OptimisticLocker˳����ȷ)+@Bean MetaObjectHandler�Զ����,mvn compile BUILD SUCCESS | (��֤����) |
| P0-001-002-001-001-001 | ���������ö���� | 2026-05-29T15:00 | ? | ����IErrorCode�ӿ�+ErrorCodeö��(ʵ��IErrorCode,��ģ��ֶ�:10000ϵͳ��/20000��֤��Ȩ/30000����У��/40000ҵ���߼�/50000���ݷ���,��SUCCESS/UNAUTHORIZED/FORBIDDEN/NOT_FOUND/INTERNAL_ERROR/PARAM_INVALID)+�޸�lombok.config Jakarta�����ռ�,mvn compile BUILD SUCCESS | cdbab8b5 |
| P0-001-002-001-001-002 | ��������������ϵ | 2026-05-29T15:15 | ? | ��֤ErrorCodeö�ٷ�����ϵ����:IErrorCode�ӿ�(getCode+getMessage)/��ģ��5��(10000ϵͳ��5��+20000��֤7��+30000����6��+40000ҵ��3��+50000����4��)/����6��(SUCCESS(0)/UNAUTHORIZED(20001)/FORBIDDEN(20003)/NOT_FOUND(10404)/INTERNAL_ERROR(10500)/PARAM_INVALID(30001))ȫ����λ/26��������Ψһ���ظ�,mvn compile BUILD SUCCESS | 0819d94b |
| P0-001-001-005-001-003 | ����У����������� | 2026-05-29T15:35 | ? | ����MybatisPlusConfigValidationTest��֤����(10��ȫPASS):@Configurationע��/@Beanע��MybatisPlusInterceptor(3��InnerInterceptor˳��TenantLine��Pagination(overflow=true)��OptimisticLocker)/@Bean MetaObjectHandler/��Ӳ����������Ϣ;actuator HTTP 200;Spring Boot�����޴��� | eda8903a |
| P0-001-001-005-002-001 | ʵ��insertFill���� | 2026-05-29T16:10 | ? | ����MyMetaObjectHandler.java(@Component):insertFill�Զ����createdAt/updatedAt/createdBy/updatedBy/tenantId/isDeleted(0),updateFillǿ��ˢ��updatedAt+strictUpdateFill����updatedBy,��MybatisPlusConfig�Ƴ�����ErpMetaObjectHandler,mvn compileͨ�� | 008c18d1 |
| P0-001-001-005-002-002 | ʵ��updateFill���� | 2026-05-29T14:55 | ? | �����ֶ�����Ϊȫ�ֹ淶(createTime/updateTime/creatorId/updaterId),updateFillǿ��ˢ��updateTime+updaterId,insertFillͬ�������ֶ���,Javadoc����@TableField(fill=FieldFill)˵��,mvn clean compile BUILD SUCCESS | 217bc44b |
| P0-001-001-005-002-003 | ��֤�Զ���� | 2026-05-29T15:08 | ? | �޸�isDeleted����Integer��Booleanƥ��ȫ�ֹ淶,updateFill��׳���޸�(�Ȼ�ȡuserId�����쳣�ж�),����MyMetaObjectHandlerTest(14��ȫPASS):insertFill 6�ֶ���֤/updateFillǿ��ˢ����֤/@Componentע��/��Ӳ����������Ϣ,mvn compile+test BUILD SUCCESS | a8a765fc |
| P0-001-002-001-002-001 | �����쳣�� | 2026-05-29T15:15 | ? | ����BaseException(abstract,extends RuntimeException,code+msg+data,ռλ����������Ϣ)+BusinessException+AuthException+ParamException+PermissionException��5���쳣��,mvn clean compile BUILD SUCCESS(37�ļ�) | d2d952ca |
| P0-001-002-001-002-002 | ʵ���Զ����쳣���쳣�����߼� | 2026-05-29T15:18 | ? | ����RT<T>ͳһ��Ӧ��+GlobalExceptionHandler(����Business/Param/Auth/Permission+Spring����У��+Sa-Token+�����쳣,��־��¼����),mvn clean compile BUILD SUCCESS(39�ļ�) | 8158d90d |
| P0-001-002-001-003-001 | ����Controller��@RequestMapping·��ע��Service | 2026-05-29T15:20 | ? | ����GlobalExceptionHandler(@RestControllerAdvice)����ConstraintViolationException����,��ȫ12���쳣(4���Զ���+SpringУ��5��+Sa-Token 3��+HTTP����/��Դ+����)ͳһ����RT+��־��¼,mvn clean compile BUILD SUCCESS(39�ļ�) | a123fce3 |
| P0-001-002-001-003-002 | ʵ�ֽӿڷ��� | 2026-05-29T15:30 | ? | ��֤GlobalExceptionHandlerʵ������(12���쳣����+RTͳһ��Ӧ+��־��¼),ǰ������������ʵ��,mvn compile BUILD SUCCESS | a123fce3 |
| P0-001-002-001-003-003 | ����ӿ��ĵ�ע�� | 2026-05-29T15:33 | ? | GlobalExceptionHandler����@Tag+17��@Operationע��,RT������@Schema�ֶ�ע��,Knife4j�ĵ�ע������,mvn compile BUILD SUCCESS | c00cddb5 |
| P0-001-002-002-001-001 | ���巺����ṹ | 2026-05-29T15:40 | ? | RT<T>��ǿ:����@JsonInclude(NON_NULL)����null�ֶ����л�+��ʽdata()����֧��RT.ok().data(xxx)+import jackson annotation,mvn compile BUILD SUCCESS | 7bf0be2a |
| P0-001-002-002-001-002 | ʵ�־�̬�������� | 2026-05-29T16:00 | ? | RT<T>��̬������������ʵ��:ok(T data)/ok()/fail(IErrorCode)/fail(int,String)/error/paramError/unauthorized/forbidden+��ʽdata()����+@JsonInclude(NON_NULL)+System.currentTimeMillis()ʱ���+IErrorCode/ErrorCode��ȷimport,mvn compile BUILD SUCCESS | a19db69e |
| P0-001-002-002-001-003 | ��д��Ԫ���� | 2026-05-29T12:15 | ? | ����RTTest(36������ȫPASS):����ok/fail��̬����+��ʽ����+@JsonInclude(NON_NULL)���л�+System.currentTimeMillis()ʱ���+Serializable+isSuccess+��ݷ���(error/paramError/unauthorized/forbidden)+��ṹ��֤,mvn test BUILD SUCCESS | 14867a12 |
| P0-001-002-002-002-001 | ���巺����ṹ | 2026-05-29T15:55 | ? | ����PageResult<T>��ҳ��Ӧ��:�ֶ�list/total/pageNum/pageSize/pages+of(IPage<T>)��MyBatis-Plusת��+of(List,total,pageNum,pageSize)�ֶ�����+empty()�շ�ҳ+@JsonInclude(NON_NULL)+@Schemaע��,mvn compile BUILD SUCCESS | fc34731f |
| P0-001-002-002-002-002 | ʵ�־�̬�������� | 2026-05-29T15:57 | ? | ��֤PageResult<T>��̬������������:of(IPage<T>)ת��MyBatis-Plus��ҳ+of(List,total,pageNum,pageSize)�ֶ�����+empty()�շ�ҳ+@JsonInclude(NON_NULL)+�ֶ�(list/total/pageNum/pageSize/pages)��ȫ,ǰ������������ʵ��,mvn compile BUILD SUCCESS | fc34731f |
| P0-001-002-002-002-003 | ��д��Ԫ���� | 2026-05-29T16:01 | ? | ����PageResultTest(34������ȫPASS):����of(IPage)ת��5��+of�ֶ�����7��+empty()4��+�ֶ�Getter/Setter3��+@JsonInclude(NON_NULL)JSON���л�5��+Serializable���л�3��+��ṹ��֤5��+RT���2��,mvn test BUILD SUCCESS | 8009a101 |
| P0-001-002-003-001-001 | ����Hibernate Validator��������MethodArgumentNotValidExceptionȫ�ֲ������� | 2026-05-29T16:11 | ? | pom.xml����spring-boot-starter-validation����+����ValidationError.java(field/message/rejectedValue+@JsonInclude(NON_NULL)+Serializable)+����GlobalExceptionHandler(MethodArgumentNotValidException/BindException/ConstraintViolationException����У���쳣��ȡFieldErrorתList<ValidationError>����RT<30001,����У��ʧ��>.data(errorList))+extractFieldName��ConstraintViolation·����ȡ�ֶ���+30�Ԫ����ȫPASS(ValidationErrorTest 20��+GlobalExceptionHandlerValidationTest 10��),mvn compile BUILD SUCCESS | fab7724c |
| P0-001-002-003-001-002 | ��֤�ֶμ�У�� | 2026-05-29T16:16 | ? | ����@Phone/@IdCard�Զ���У��ע��+PhoneValidator/IdCardValidator(ConstraintValidatorʵ��)+CustomValidationAnnotationTest(20��ȫPASS:@Phone�Ϸ�/�Ƿ�/null/��ֵ+@IdCard 18λ/15λ/X/�Ƿ�/null+Handler����code=30001)+��֤pom.xml validation����+GlobalExceptionHandler����У���쳣����+ErrorCode.PARAM_INVALID(30001)ȫ��·ͨ��,mvn compile+test 50/50 PASS | c1cca8ec |
| P0-001-002-003-002-001 | ����ע��Ԫ���� | 2026-05-29T16:30 | ? | ����@EnumValue(enumClass+method+@Repeatable)/EnumValueValidator(����ö��ֵУ��)+@NotEmptyList/NotEmptyListValidator(Collection�ǿ�У��)+Phone/IdCard i18n��Ϣ����+messages.properties/messages_en_US.properties���ʻ�����,mvn compile BUILD SUCCESS | d95f5db4 |
| P0-001-002-003-002-002 | ʵ��ע�⴦���� | 2026-05-29T16:42 | ? | ��֤@Phone/@IdCard/@EnumValue/@NotEmptyList�ĸ��Զ���ע��+��ӦConstraintValidatorȫ����λ,����ע��@Target(FIELD/PARAMETER)+@Retention(RUNTIME)+i18n��Ϣ����,CustomValidationAnnotationTest 20��ȫPASS,mvn compile BUILD SUCCESS | 3974dbc1 |
| P0-001-003-001-001-001 | ����@Configuration������@Beanע�᷽ʽ | 2026-05-29T17:00 | ? | ����DataSourceConfig.java(@Configuration+@Bean DataSource)+HikariCP���ӳ�7�����(��С����5/���20/���г�ʱ300000/�����������1200000/���ӳ�ʱ30000/����ErpHikariPool/й¶���60000)+application-dev.yml����hikari���ÿ�,mvn compile BUILD SUCCESS | 93cd22c1 |
| P0-001-003-001-001-002 | �������԰� | 2026-05-29T17:10 | ? | ��֤application-dev.yml HikariCP 7����������ȫ����ȷ��(ǰ��������ʵ��),mvn compileͨ�� | bb1ce616 |
| P0-001-003-002-002-002 | �����ֶ������Ⱦ | 2026-05-29T17:20 | ? | ����QueryHelper.javaͨ�ò�ѯ����������(buildLikeWrapper ORģ����ѯ+buildDateRangeWrapper���ڷ�Χ+buildEnumWrapperö��ɸѡ+��ʽ����LambdaQueryWrapper),mvn compileͨ�� | 10f38f4d |
| P0-001-003-002-001-001 | ����Mapper�ӿ� | 2026-05-29T17:40 | ? | ����BaseMapperX.java��չMapper����(�̳�BaseMapper<T>,�ṩselectPageByCondition/selectOneById/insertBatch/updateBatchById/deleteByIds 5��ͨ�÷���,����MyBatis-Plus Db������ʵ����������),mvn compileͨ�� | 659d74c4 |
| P0-001-003-002-001-002 | ��дXMLӳ���ļ� | 2026-05-29T17:30 | ? | BaseMapperXʹ��default����ί��MyBatis-Plus API(SqlHelper),ͨ�÷��ͽӿ�����XMLӳ��,����5��������ʵ�ֲ�����ͨ�� | d23545b3 |
| P0-001-003-001-001-003 | ����У����������� | 2026-05-29T18:00 | ? | �ع�DataSourceConfig:Ӳ�����@Value��YAML��ȡHikariCP����+@Validated+@Min/@MaxУ��ע��,application-dev.yml 7��HikariCP��������,��/actuator/env��֤ | 456e9e7f |
| P0-001-003-001-002-001 | ����ע��Ԫ���� | 2026-05-29T17:38 | ? | ����DS.java(@Target TYPE/METHOD+@Retention RUNTIME+value() default "master")+DynamicDataSource(extends AbstractRoutingDataSource+DataSourceContextHolder ThreadLocal)+����DataSourceConfig(@Bean masterDataSource+@Primary @Bean dataSource��װDynamicDataSource),mvn compile BUILD SUCCESS | 0f86b0e7 |
| P0-001-003-001-002-002 | ʵ��ע�⴦���� | 2026-05-29T18:30 | ? | ����DataSourceAspect.java(@Aspect+@Component+@Order(-1)+@Around dsPointcut,��ȡ@DSע��value�л�����Դ,finally����ThreadLocal),mvn compile BUILD SUCCESS | d8c2d913 |
| P0-001-003-002-002-001 | ������������ | 2026-05-29T18:35 | ??��? | ԭ����(�����ĵ���Ϣ����),���Զ���ģʽ���:����QueryHelperTest.java(54�Ԫ����ȫPASS,����buildLikeWrapper/buildDateRangeWrapper/buildEnumWrapper/create/��ʽ����/���Ͱ�ȫ/���ձ�׼) | (�������) |
| P0-001-003-002-002-003 | ʵ��ͨ�ò�ѯ���������ѯҳ�������� | 2026-05-29T18:35 | ??��? | ԭ����(�����ĵ���Ϣ����),���Զ���ģʽ���:QueryHelper����buildSearchWrapper/applyKeywordCondition/applyDateRangeCondition/applyEnumCondition����ʵ�ֲ�ѯҳ��������,����13�����ȫPASS | (�������) |
| P0-001-003-003-001-001 | ����Service�ӿ� | 2026-05-29T18:50 | ? | ����IServiceX.java(�̳�IService<T>,6��default����:createBatch/updateBatch/pageList/getOneOrThrow/existsById/checkExists)+PageQuery.javaͨ�÷�ҳ��ѯ������,mvn compile BUILD SUCCESS | 353c343e |
| P0-001-003-003-001-002 | ʵ��ServiceImpl | 2026-05-29T19:00 | ? | ����ServiceImplX.java(�̳�ServiceImpl<M,T>,ʵ��IServiceX<T>,��BaseMapperX��IServiceX),mvn compile BUILD SUCCESS | a267e5b0 |
| P0-001-003-003-001-003 | ҵ�������� | 2026-05-29T19:10 | ? | IServiceX.java 6��default��������(createBatch/updateBatch/pageList/getOneOrThrow/existsById/checkExists),mvn compileͨ�� | 1ccc659c |
| P0-001-003-003-002-001 | ����Service�ӿ� | 2026-05-29T19:20 | ? | ����BaseCrudService.java������(4����+validateCreate/validateUpdateģ��+create/update/delete/getById/pageList 5��CRUD����+MapStructת������+@Transactionalд����),mvn compile BUILD SUCCESS | c3ff7cde |
| P0-001-003-003-002-002 | ʵ��ServiceImpl | 2026-05-29T19:20 | ? | BaseCrudService.java��������ʵ��(extends ServiceImplX<BaseMapperX<E>,E>,validateCreate/validateUpdateģ�巽��,create/update/delete/getById/pageList 5��CRUD����,MapStructת�����󷽷�,@Transactionalд����),mvn compileͨ�� | c3ff7cde |
| P0-001-003-003-002-003 | ҵ��У���߼� | 2026-05-29T18:32 | ? | BaseCrudService.java��֤ͨ��(5��CRUD����ǩ������+validateCreate/validateUpdateģ��+MapStructת������+@Transactionalд����+BusinessException���ڼ��),mvn compile BUILD SUCCESS | 8620f9ac | adb00c90 |
| P0-001-004-001-001-003 | ����У����������� | 2026-05-29T19:35 | ? | SaTokenConfig.java(SaServletFilter+SaInterceptor˫����,�ų�login/logout/knife4j·��,RT.failδ��¼��Ӧ),mvn compileͨ�� | 1df2a8a5 |
| P0-001-004-002-002-002 | ʵ�ֺ��Ĵ����߼� | 2026-05-29T20:00 | ? | LogicEnum(AND/OR)+@RequirePermission(valueȨ��������,logic�߼������Ĭ��AND)+PermissionAspect(@Around����,StpUtil.checkPermissionAnd/OrУ��,ʧ��NotPermissionException��403),mvn compileͨ�� | 06c219e8 |
| P0-001-004-001-001-001 | ����@Configuration������@Beanע�᷽ʽ | 2026-05-29T22:00 | ? | SaTokenConfig(@Configuration)+SaServletFilter(@Bean����/api/**�ų�login/logout/doc.html/v3)+δ��¼RT.fail(ErrorCode.UNAUTHORIZED)+SaInterceptor·������+isAnnotationע���Ȩ˫���� | 1df2a8a5 |
| P0-001-004-001-001-002 | �������԰� | 2026-05-29T19:01 | ? | SaTokenConfig.java @Value��exclude-paths(@ConfigurationProperties���)+application.yml sa-token.exclude-paths������,mvn compile BUILD SUCCESS | 8620f9ac |
| P0-001-004-001-002-001 | ����ӿ�·���뷽��ǩ�� | 2026-05-29T19:10 | ? | StpInterfaceImpl(@Component implements StpInterface)+getPermissionList/getRoleList Redis����(satoken:permission/role:{loginId},5minTTL)+clearCache+spring-boot-starter-data-redis����,mvn compile BUILD SUCCESS | f4fcdc9f |
| P0-001-004-001-002-002 | ʵ�ֽӿ��߼� | 2026-05-29T20:10 | ? | StpInterfaceImpl.java����ʵ�֣�getPermissionList/getRoleList Redis��������+DB����+clearCache�������,mvn compileͨ�� | d83c1532 |
| P0-001-004-002-001-001 | ����ӿ�·���뷽��ǩ�� | 2026-05-29T22:30 | ? | ����SaInterceptorConfig.java(@Configuration+WebMvcConfigurer+SaInterceptor+pathPatterns+CORS localhost:5173 Authorization),mvn compileͨ�� | 7ceff390 |
| P0-001-004-002-001-002 | ʵ�ֽӿ��߼� | 2026-05-29T23:00 | ? | SaInterceptorConfig.java����ʵ����֤��addInterceptors(SaInterceptor+SaRouter.match+notMatch+check)+addCorsMappings(allowCredentials+exposedHeaders), application.yml sa-token����timeout=2592000/active-timeout=1800,mvn compileͨ�� | 741e2419 |
| P0-001-004-002-002-003 | ���ɲ�����֤ | 2026-05-29T19:47 | ? | PermissionAspectIntegrationTest(19�����ɲ���ȫ��ͨ��,AND/OR�߼�+���г���+ע����֤+�쳣��֤+pom.xml����H2 test scope+test application.yml H2�ڴ����ݿ�����),mvn compile+mvn test BUILD SUCCESS | 37e797c8 |

### P0-001-005 - ���������������

| ������ | �������� | ���ʱ�� | ״̬ | ժҪ | Git SHA |
|---------|---------|---------|:---:|------|---------|
| P0-001-005-001-001-001 | ��дCREATE TABLE sys_code_rule����� | 2026-05-29T20:00 | ? | ����V1__create_sys_code_rule.sql:sys_code_rule����(id/rule_code/rule_name/module_code/description/separator/current_value/is_enabled+10��ͨ���ֶ�)+sys_code_rule_segment�ӱ�(rule_id FK/segment_type/segment_order/segment_value/segment_length/segment_format+10��ͨ���ֶ�)+uk_rule_module����Ψһ����(rule_code,tenant_id)WHERE is_deleted=false+idx_segment_order����(rule_id,segment_order)+COMMENT����,mvn compile BUILD SUCCESS | 5510b3ce |
| P0-001-005-001-001-002 | ��������������Լ�� | 2026-05-29T20:10 | ? | ��֤V1__create_sys_code_rule.sql:PRIMARY KEY(pk_sys_code_rule/pk_sys_code_rule_segment)+FOREIGN KEY(fk_segment_rule_id)+uk_rule_module(rule_code,tenant_id WHERE is_deleted=FALSE)+idx_segment_order(rule_id,segment_order)ȫ����λ,mvn compileͨ�� | 8925546a |
| P0-001-005-001-002-001 | ��дCREATE TABLE DDL | 2026-05-29T21:30 | ? | ��֤sys_code_rule_segment�ӱ�DDL����(rule_id FK/segment_type/segment_order/segment_value/segment_length/segment_format+ͨ���ֶ�+idx_segment_order����),ǰ�������Ѵ���ȫ��DDL | 4270d90d |
| P0-001-005-001-002-002 | ����������Լ�� | 2026-05-29T22:00 | ? | V1__create_sys_code_rule.sql����uk_segment_rule_orderΨһ��������(rule_id,segment_order,tenant_id WHERE is_deleted=FALSE)��ֹͬ����ͬ�⻧�������ظ�,�ӱ�Լ������:PK+FK+idx_segment_order+uk_segment_rule_order,mvn compile BUILD SUCCESS | 8f764bda |
| P0-001-005-001-003-001 | ��дCREATE INDEX��� | 2026-05-29T22:30 | ? | ��֤V1__create_sys_code_rule.sql DDL����:uk_rule_module(rule_code,tenant_id)Ψһ����+idx_segment_order(rule_id,segment_order)��������+uk_segment_rule_orderΨһ��������+�����ֶ�ע��+Flyway��ʽ,mvn compileͨ�� | 44ca7b1c |
| P0-001-005-001-003-002 | ִ��DDL�ű� | 2026-05-29T23:00 | ? | ��erp_dev��ִ��V1__create_sys_code_rule.sql:sys_code_rule����(17�ֶ�)+sys_code_rule_segment�ӱ�(16�ֶ�)+5������ȫ�������ɹ�,information_schema��֤ͨ�� | 37efc091 |
| P0-001-004-003-001-001 | ����@Configuration������@Beanע�᷽ʽ | 2026-05-29T23:30 | ? | pom.xml����sa-token-redis-jackson����(1.39.0)+application.yml����Redis Lettuce���ӳ�����(max-active=8/max-idle=8/min-idle=0)+application-dev.yml����poolֵ | 238676c5 |
| P0-001-004-003-001-002 | �������԰� | 2026-05-29T23:45 | ? | ��֤pom.xml sa-token-redis-jackson����(1.39.0)+application.yml Redis Lettuce���ӳ�(max-active=8/max-idle=8/min-idle=0)+Jackson�������л�(yyyy-MM-dd HH:mm:ss)+Sa-Token Redis�洢�Զ���Ч,mvn compile BUILD SUCCESS | (��֤����) |
| P0-001-004-003-001-003 | ����У����������� | 2026-05-29T23:55 | ? | ����JacksonConfig����FAIL_ON_SELF_REFERENCES����Sessionѭ������+SaTokenProperties���Ͱ�ȫ���ð���@Validated����У��+ע��JavaTimeModuleָ��LocalDateTime���л���ʽ,mvn compile BUILD SUCCESS | 87e7830d |
| P0-001-004-003-002-001 | ʵ�������û���ѯ | 2026-05-29T20:50 | ? | ����SessionService(@Service)ʵ��listOnline/fForceLogout/renewSession/getCurrentUser 4������+LoginUserVO(5�ֶ�@Builder)+mvn compile BUILD SUCCESS | 8620f9ac |
| P0-001-004-003-002-002 | ʵ��ǿ�����߻Ự��ʱ���� | 2026-05-29T21:00 | ? | ��֤SessionService 4������(forceLogout��logoutByTokenValue/renewSession��renewTimeout/getCurrentUser��getSession/listOnline��searchSessionId)ȫ��ʵ���ұ���ͨ�� | 8620f9ac |
| P0-001-004-003-002-003 | ��֤�Ự���� | 2026-05-29T21:10 | ? | ��֤SessionService 4������,���ֲ��޸�buildLoginUserVO token������Bug,mvn compile BUILD SUCCESS | (pending) |
| P0-001-005-001-003-003 | ��֤������Լ�� | 2026-05-29T23:59 | ? | ��֤V1__create_sys_code_rule.sql DDL����:sys_code_rule(17�ֶ�)+sys_code_rule_segment(16�ֶ�)+uk_rule_module����Ψһ����(rule_code,tenant_id)+idx_segment_order(rule_id,segment_order)+uk_segment_rule_order(rule_id,segment_order,tenant_id)+ȫ�ֹ淶�ֶ�������֤+�״���ʾ3��ȫ��ͨ��,mvn compileͨ�� | (pending) |
| P0-001-005-002-001-001 | ����Entity�� | 2026-05-29T21:16 | ? | ����BaseEntity����(11��ͨ���ֶ�+@TableLogic+@Version)+SysCodeRuleʵ��(7ҵ���ֶ�)+SysCodeRuleSegmentʵ��(6ҵ���ֶ�)+SysCodeRuleDTO(��CreateDTO/UpdateDTO/QueryDTO/SegmentDTO)+SysCodeRuleVO(��ListVO/DetailVO/SegmentVO),mvn compile BUILD SUCCESS (75 source files) | 60647c4b |
| P0-001-005-002-001-002 | ����DTO�� | 2026-05-29T21:35 | ? | ��֤SysCodeRuleDTO(CreateDTO @NotBlank ruleCode/@NotNull segments+UpdateDTO+QueryDTO��ҳ/SegmentDTO)+SysCodeRuleVO(ListVO+DetailVO+SegmentVO+@JsonFormat���ڸ�ʽ),Entity-DDL�ֶ�һһ��Ӧ,@TableLogic/@TableField��ȷ,mvn compileͨ�� | 4a2cf1a3 |
| P0-001-005-002-001-003 | ����VO�� | 2026-05-29T22:00 | ? | SysCodeRuleVO��ǿ:ListVO/DetailVO����isEnabledName�ֵ䷭���ֶ�+SegmentVO����segmentTypeName�ֵ䷭���ֶ�,@JsonFormat���ڸ�ʽ,mvn compileͨ�� | 48aec71a |
| P0-001-005-002-002-001 | ����Mapper�ӿ� | 2026-05-29T23:30 | ? | SysCodeRuleMapper extends BaseMapperX(selectRuleWithSegments�������ӱ�+selectByRuleCode�������ѯ+updateCurrentVersion�ֹ���)+SysCodeRuleMapper.xml(resultMap+LeftJoin+�ֹ���UPDATE),mvn compileͨ�� | 45488253 |
| P0-001-005-002-002-002 | ��дXMLӳ�� | 2026-05-29T23:55 | ? | SysCodeRuleMapper.xml����ʵ��:BaseResultMap+RuleWithSegmentsResultMap(��segments collectionǶ��)+selectRuleWithSegments��������+selectByRuleCode�������ѯ+updateCurrentVersion�ֹ���UPDATE,namespace��ȷ,mvn compileͨ�� | (��֤����) |
| P0-001-005-002-002-003 | ��д�Զ����ѯ���� | 2026-05-29T23:59 | ? | SysCodeRuleMapper�Զ����ѯ������֤:selectRuleWithSegments����+selectByRuleCode�������ѯ+updateCurrentVersion�ֹ���,ǰ����������Ѿ�λ,mvn compileͨ�� | (��֤����) |
| P0-001-005-002-003-001 | ����Service�ӿ� | 2026-05-29T12:00 | ? | SysCodeRuleService(BaseCrudService��չ:create/update+ruleCodeΨһ��У��+segment��������/delete����ɾ��+preview/generate/refreshCache���󷽷�)+SysCodeRuleServiceImpl(Redis�ֲ�ʽ��+����)+SysCodeRuleSegmentMapper,mvn compileͨ�� | 5dba5dc9 |
| P0-001-005-002-003-002 | ʵ��ServiceImpl | 2026-05-29T12:30 | ? | SysCodeRuleServiceImpl��֤���:Redis�ֲ�ʽ��(code:lock:{ruleCode})+3������(100ms���)+BusinessException+refreshCache�������+@Transactionalд����,��ܾ���,���������߼����ν�����(P0-001-005-003)���� | 31ddd74f |
| P0-001-005-002-003-003 | ҵ��У���߼� | 2026-05-29T13:00 | ? | ������У��(validateSegments:����1-4/����ȥ��/�̶���ֵ����/���жγ��ȱ���)+preview(��������Ρ�buildCodeԤ��ģʽ)+generate(�ֲ�ʽ��+3������+����У��+Redis INCR��������+buildCode��ʽģʽ)+refreshCache(���cache+seq),mvn compileͨ�� | 24ff9e21 |
| P0-001-005-003-001-001 | ʵ�̶ֹ������ڶ����ж��Զ�������ν����� | 2026-05-29T14:00 | ? | ����SegmentParser�ӿ�+4��ʵ��(FixedSegmentParser�̶��ַ���/DateSegmentParser DateTimeFormatter/SequenceSegmentParser����+SequenceGenerator�ӿ�/VariableSegmentParser������Map)+SegmentParserFactory·��+SegmentParseContext������,mvn compileͨ�� | (pending) |
| P0-001-005-003-001-002 | ʵ�ֶν����������߼� | 2026-05-29T14:30 | ? | DateSegmentParser����DateTimeFormatter����+��ʽУ��BusinessException/SequenceSegmentParser����3������(50ms����)ȫʧ����BusinessException/SegmentParserFactory·��δ֪������BusinessException,mvn compileͨ�� | 4408a5b8 |
| P0-001-005-003-001-003 | ��֤���ν��� | 2026-05-29T22:56 | ? | SegmentParserVerificationTest(22��ȫPASS�̶���+���ڶ�+������+����·��)+SequenceSegmentParserTest(9��ȫPASSĬ��λ��+Ԥ��ģʽ+����+���Ժľ����쳣),31/31 PASS,mvn compileͨ�� | 2e1ce949 |
| P0-001-005-003-002-001 | ʵ��Redis INCR�ֲ�ʽ���� | 2026-05-29T23:08 | ? | SequenceGenerator:Redis INCRԭ������(key=code:seq:{ruleCode}:{yyyyMMdd},EX=86400)+�ֲ�ʽ��(key=code:lock:{ruleCode})+DB�ֹ�������(updateCurrentVersion����3��)+getNext����;SequenceSegmentParserTest 9/9 PASS,mvn compileͨ�� | 6cb67db6 |
| P0-001-005-003-002-002 | ʵ�����к������߼� | 2026-05-29T23:32 | ? | SequenceGenerator.java����ʵ��:getNext(ruleCode,length)�����ַ���+redisNext Redis INCRԭ������+dbNext DB�ֹ�������3������+�ֲ�ʽ��+�������Զ�����,mvn compile BUILD SUCCESS | 987d33ab |
| P0-001-005-003-002-003 | ��֤���к����� | 2026-05-29T23:42 | ? | SequenceGeneratorTest 16/16 PASS:Redis�ֲ�ʽ��(7��)+INCR����(2��)+DB��������3�����쳣(5��)+�ۺ���֤(2��),mvn compile BUILD SUCCESS | ec1e624f |
| P0-001-005-003-003-001 | ʵ�ֱ���Ԥ���߼� | 2026-05-29T23:55 | ? | CodePreviewService.preview(ruleId,count)ʹ��SegmentParserFactory����ģʽ,Ԥ��ģʽ���������к�,mvn compile BUILD SUCCESS | 6efd4c85 |
| P0-001-005-003-003-002 | ʵ�ֱ���Ԥ�������߼� | 2026-05-29T23:58 | ? | ��ǿCodePreviewService�����߼�:buildEffectiveVariables��������Map����SegmentParseContext������preview(ruleId,count,variables)����֧���Զ��������SequenceSegmentParserԤ����Xռλ��,mvn compile BUILD SUCCESS | a1afb658 |


| P0-001-005-003-003-003 | ��֤����Ԥ�� | 2026-05-30T00:10 | ? | ��֤CodePreviewService.javaԤ���߼���ȷ:Ԥ��ģʽ�������жβ��������к�(SequenceSegmentParser����Xռλ��)+�Զ������+�ָ���ƴ��+null����/�ն��쳣����,�޸�SequenceSegmentParserTestԤ����������ֵ(00001��XXXXX),47/47 codegen����ȫPASS,mvn compile BUILD SUCCESS | 7ab1fe83 |
| P0-001-005-003-004-001 | ʵ�ֱ��������߼� | 2026-05-30 | ? | ����CodeGenerateService.java(Redis�ֲ�ʽ��code:lock:{ruleCode} 3s��ʱ+�������+������+parserƴ��+Redis INCR���к�������+finally�ͷ���+3��������BusinessException),mvn compile BUILD SUCCESS | bde51146 |
| P0-001-005-003-004-002 | ʵ�ֱ������ɺ����߼� | 2026-05-30 | ? | CodeGenerateService.java����001����������ʵ��(�ֲ�ʽ��+�������+������+parserƴ��+INCR���к�+����),mvn compile BUILD SUCCESS,�����߼�ȫ������ | bde51146 |
| P0-001-005-003-004-003 | ��֤�������� | 2026-05-30T00:17 | ? | ����CodeGenerateServiceTest.java(24��ȫPASS):Redis�ֲ�ʽ����֤(5��)+���������߼���֤(6��)+���к�key��ʽ����������֤(5��)+ʧ��������֤(5��)+�ۺϳ�����֤(3��),mvn compile BUILD SUCCESS,CodeGenerateService���ձ�׼ȫ��ͨ�� | (pending) |
| P0-001-005-004-001-001 | ����ӿ�·���뷽��ǩ�� | 2026-05-30T00:10 | ? | ����SysCodeRuleController.java(@RestController @RequestMapping /api/system/code-rules):8��CRUD+Ԥ��+���ɽӿ�,���з���@RequirePermission+@Operationע����ȫ,RESTful·���淶,mvn compile BUILD SUCCESS | (pending) |
| P0-001-005-004-001-002 | ʵ�������޸�ɾ������ | 2026-05-30T00:30 | ? | SysCodeRuleController.javaȫ��7���ӿڷ���ʵ�����(create/update/delete/getById/pageList/preview/generate),@RequirePermission+@Operation��ȫ,RESTful�淶,Service��CRUD����(@Transactional+�δ���+Ψһ��У��),mvn compile BUILD SUCCESS | (pending) |
| P0-001-005-004-001-003 | ʵ�ֲ�ѯ���� | 2026-05-30 | ? | ��֤SysCodeRuleController��ѯ��������(getById/pageList/preview/generate),@RequirePermission+@Operation��ȫ,RESTful·���淶,mvn compileͨ�� | (��֤����) |
| P0-001-005-004-002-001 | ����ӿ�·���뷽��ǩ�� | 2026-05-30T12:00 | ? | SysCodeRuleController.javaԤ��/���ɽӿ�·��(previvew/generate)�Ѿ�λ,@RequirePermission+@Operation��ȫ,RESTful·���淶,mvn compileͨ�� | (pending) |
| P0-001-005-004-002-002 | ʵ�ֽӿ��߼� | 2026-05-30T12:30 | ? | SysCodeRuleController.javaȫ��7���ӿ�(CRUD+Ԥ��+����)����ʵ��,���з���@RequirePermission+@Operation��ȫ,RESTful�淶,mvn compileͨ�� | (pending) |
| P0-001-005-004-003-001 | ����ӿ�·���뷽��ǩ�� | 2026-05-30T13:00 | ? | SysCodeRuleController.java���ɽӿ�·��(generate+preview)�Ѿ�λ,@RequirePermission+@Operation��ȫ,RESTful·���淶,mvn compileͨ�� | (pending) |
| P0-001-005-004-003-002 | ʵ�ֽӿ��߼� | 2026-05-30T13:30 | ? | SysCodeRuleController.javaȫ��7���ӿ�(create/update/delete/getById/pageList/preview/generate)����ʵ���߼�,���з���@RequirePermission+@Operation��ȫ,RESTful·��/api/system/code-rules,mvn compile BUILD SUCCESS | (pending) |

### P0-001-006 - ������ͼ�����������

| ������ | �������� | ���ʱ�� | ״̬ | ժҪ | Git SHA |
|---------|---------|---------|:---:|------|---------|
| P0-001-006-001-001-001 | ��дCREATE TABLE sys_data_view����� | 2026-05-30T01:00 | ? | ����V2__create_sys_data_view.sql:sys_data_view����(id/view_codeΨһ/view_name/source_table/source_type(1��2SQL)/source_sql/description+10��ͨ���ֶ�)+sys_data_view_field�ӱ�(view_id FK/field_code/field_name/field_type/field_order/is_searchable/is_sortable/is_visible/search_type/search_component+10��ͨ���ֶ�)+uk_view_code����Ψһ����(view_code,tenant_id)+idx_field_view_order����(view_id,field_order)+uk_field_view_code����Ψһ����(view_id,field_code,tenant_id)+COMMENT����+�ع��ű� | 8620f9ac |
| P0-001-006-001-001-002 | ��������������Լ�� | 2026-05-30T02:00 | ? | V2__create_sys_data_view.sql����idx_sys_data_view_tenant�⻧��ѯ����+idx_sys_data_view_field_tenant�⻧��ѯ����,����/Ψһ����/���Լ����֤���� | b8c42093 |
| P0-001-006-001-002-001 | ��дCREATE TABLE DDL | 2026-05-30T03:00 | ? | ��֤sys_data_view_field�ӱ�DDL(V2__create_sys_data_view.sql)�ֶ�/Լ��/����/COMMENT�����Ϲ� | 4cfd2487 |
| P0-001-006-001-002-002 | ����������Լ�� | 2026-05-30T03:30 | ? | V2__create_sys_data_view.sql sys_data_view_field�ӱ�PK+FK+uk_field_view_code+idx_field_view_order+idx_tenantȫ������Լ���Ѿ�λ,mvn compileͨ�� | 60589236 |

| P0-001-006-001-003-001 | ��дCREATE INDEX��� | 2026-05-30T04:00 | ? | V2__create_sys_data_view.sql����4�������Ż�����(idx_sdv_source_table/idx_sdv_created_at/idx_sdvf_field_code/idx_sdvf_is_searchable),ȫ����is_deleted=false������������ | 224a0ed1 |
| P0-001-006-001-003-002 | ִ��DDL�ű� | 2026-05-30T01:00 | ? | V2__create_sys_data_view.sql DDL����(����9�ֶ�+�ӱ�11�ֶ�+�����ֶ�+3������+COMMENT+�ع�ע��),����ȫ�����ݿ�淶,mvn compileͨ�� | 0c0084b2 |
| P0-001-006-001-003-003 | ��֤������Լ�� | 2026-05-30T04:30 | ? | ��̬��֤V2__create_sys_data_view.sql 12������Լ��ȫ���Ϲ�:sys_data_view 5��(pk+uk_view_code��tenant_id+3����������)+sys_data_view_field 7��(pk+fk+uk_field_view_code��tenant_id+4����������),mvn compileͨ�� | (pending) |
| P0-001-006-002-001-001 | ����Entity�� | 2026-05-30 | ? | ����SysDataView.java(@TableName sys_data_view,6�ֶ�+BaseEntity�̳�)+SysDataViewField.java(@TableName sys_data_view_field,10�ֶ�+BaseEntity�̳�),@tableId ASSIGN_ID+@TableLogic+@Version��ȫ,mvn compileͨ�� | 272f5901 |
| P0-001-006-002-001-002 | ����DTO�� | 2026-05-30T05:00 | ? | ����SysDataViewDTO.java(CreateDTO @NotBlank viewCode/viewName/sourceTable+UpdateDTO+QueryDTO��ҳ)+SysDataViewFieldDTO.java(CreateDTO @NotNull viewId+@NotBlank fieldCode/fieldName+UpdateDTO+QueryDTO��ҳ),��ѭSysCodeRuleDTOǶ�׾�̬��ģʽ,mvn compileͨ�� | a5c43472 |
| P0-001-006-002-001-003 | ����VO�� | 2026-05-30T06:00 | ? | ����SysDataViewVO.java(ListVO+DetailVO��fields�б�+sourceTypeName�ֵ䷭��+@JsonFormat����)+SysDataViewFieldVO.java(ListVO+DetailVO+fieldTypeName�ֵ䷭��+@JsonFormat����),��ѭSysCodeRuleVOǶ�׾�̬��ģʽ,mvn compileͨ�� | c40b404c |
| P0-001-006-002-002-001 | ����Mapper�ӿ� | 2026-05-30T06:30 | ? | ����SysDataViewMapper.java(selectViewWithFields����+selectByViewCode�������ѯ)+SysDataViewFieldMapper.java(selectVisibleFields��ѯ�ɼ��ֶ�)+SysDataViewMapper.xml(3��resultMap+2���Զ���SQL+����ӳ��),mvn compileͨ�� | 69481f83 |
| P0-001-006-002-002-002 | ��дXMLӳ�� | 2026-05-30T01:39 | ? | SysDataViewMapper.xml����ǰ�����񴴽�������ͨ��:3��resultMap(Base+FieldBase+ViewWithFields��collection)+2��SQLƬ��+selectViewWithFields����LEFT JOIN+selectByViewCode�������ѯ,mvn compileͨ�� | 9de3abca |
| P0-001-006-002-002-003 | ��д�Զ����ѯ���� | 2026-05-30T08:00 | ? | ����SysDataViewFieldMapper.xml(selectVisibleFieldsǨ�Ƶ�XML)+����SysDataViewFieldMapper.java�Ƴ�@Selectע��,�����Զ����ѯȫ����XML�ж���,mvn compileͨ�� | 0f12361e |
| P0-001-006-002-003-001 | ����Service�ӿ� | 2026-05-30T02:00 | ? | ����SysDataViewService.java(abstract class extends BaseCrudService:create/update viewCodeΨһ��У��+source_sql������У���ֹDROP/DELETE/UPDATE/INSERT��12�ؼ���+delete����ɾ��fields+escapeFieldName˫����ת���ע��+getViewMeta/executeView���󷽷�)+SysDataViewServiceImpl.java(@Service:getViewMeta��ѯ��ͼ���ɼ��ֶ�Ԫ����+executeView��̬SQL����ִ�з�ҳpageSize��100+Ĭ������create_time DESC+is_deleted��ɾ������),mvn compileͨ�� | dc88b0a9 |
| P0-001-006-002-001-001 | ����Entity�� | 2026-05-30T05:00 | ? | ����SysDataView.java(6ҵ���ֶ�+BaseEntity�̳�)+SysDataViewField.java(10ҵ���ֶ�+BaseEntity�̳�),@TableName+@TableLogic+@TableId ASSIGN_ID,�ֶ���DDLһһ��Ӧ,mvn compileͨ�� | 272f5901 |

### д���ʽ�����˱����ϸ����أ�
**ÿ��������ɼ�¼**��һ��һ������

```
| P0-001-001-001-001-001 | ִ��Spring Initializr������Ŀ | 2026-05-28T14:30:00 | ? | ����Spring Boot��Ŀ�Ǽ� | a1b2c3d4 |
```

**ģ����ɱ��**����ĳģ�������Ҷ������ȫ�� ? ���ڸ�ģ���¼���ĩβ׷�ӣ���

```
### ģ�����: P0-001 ?
```

> **��Ҫ**��ģ����ɱ���ǵ������ж�ģ���Ƿ���ɵ�Ψһ���ݡ�
> û�����б�ǣ�����������Ϊ��ģ����δ��ɣ���������������������ģ�顣
> ��ʽ���뾫ȷƥ�� `ģ�����: P?-??? ?`������������޷�ʶ��

| P0-001-006-002-003-002 | ʵ��ServiceImpl | 2026-05-30T00:05 | ? | SysDataViewServiceImpl.javaʵ��(getViewMeta��ͼԪ����+executeView��̬SQL��ѯ+validateSourceSql������+deleteFieldsByViewId����ɾ��+escapeFieldNameת��),mvn compileͨ�� | dc88b0a9 |
| P0-001-006-002-003-003 | ҵ��У���߼� | 2026-05-30T10:10 | ? | ��֤SysDataViewService/SysDataViewServiceImplҵ��У���߼�:SQL������12�ؼ���+�ֶ�˫����ת��+��ҳ��100+Ĭ��create_time DESC+viewCodeΨһ��+SELECT/FROM�غ�+����ɾ��+getViewMeta/executeView,ȫ7������ͨ��,mvn compileͨ�� | (��֤����) |
| P0-001-006-003-001-001 | ʵ��SQL��̬���� | 2026-05-30T10:30 | ? | DataViewSqlBuilder.java(@Component):buildSelectSql(viewId,queryParams)������+SQL������У��+��̬WHERE(=,LIKE,BETWEEN)+PostgreSQL˫����ת��+is_deleted=FALSE+pageSize��100,mvn compileͨ�� | 6e6bf46e |
| P0-001-006-003-001-002 | ʵ��SQL���������߼� | 2026-05-30T11:00 | ? | DataViewSqlBuilder.java�����߼�����(validateSqlWhitelist+validateSourceSql+escapeFieldName+resolveSortField+buildBetweenCondition),ȫ3������ͨ��,mvn compileͨ�� | (��֤����) |
| P0-001-006-003-001-003 | ��֤SQL���� | 2026-05-30T11:35 | ? | DataViewSqlBuilderVerificationTest.java(34����):SQL������11��+�ֶ�ת��5��+��ҳ����7��+��������4��+�״���ʾ2��+�߽�2��+�쳣3��,���ֲ��޸�CREATE�ؼ��ִʱ߽�����bug,mvn testͨ�� | 82ff6ca5 |
| P0-001-006-003-002-001 | ������������ | 2026-05-30T12:08 | ? | DataViewQueryParser.java(@Component):parseConditions/parseOne������+8�ֲ�����(eq/ne/gt/gte/lt/lte/like/between/in)+LIKEת��%/_+between����У��+in��100����+�ֶ�˫����ת��,34����ͨ�� | 8f731bd3 |
| P0-001-006-003-002-002 | �����ֶ������Ⱦ | 2026-05-30T14:45 | ?? | �����ظ�-DataViewQueryParser.java����P0-001-006-003-002-001�д������ύ(8f731bd3) | (�����ظ�-ǰ�����������) |
| P0-001-007-001-001-001 | ��дCREATE TABLE DDL | 2026-05-30T15:00 | ? | V3__create_sys_param.sql(sys_param��:9ҵ���ֶ�+10ͨ���ֶ�+Ψһ����uk_category_key+idx_category+idx_sys_param_tenant) | 20333511 |
| P0-001-006-003-003-002 | ʵ�ַ�ҳ��������߼� | 2026-05-30T16:00 | ? | DataViewPagingExecutor.java(@Component):execute(viewCode,PageQuery)������+MyBatis-Plus Page��װ+pageSize��100����+ί��DataViewSqlBuilder���������У��+Ĭ��create_time DESC,mvn compileͨ�� | (���ύ) | 3b84b1f1 |
| P0-001-006-003-003-001 | ʵ�ַ�ҳ����ִ�� | 2026-05-30T17:30 | ? | DataViewPagingExecutor.java��ǿ:execute()��������sortField/sortOrder��SqlBuilder;PageQuery.java����sortField/sortOrder�ֶ�;���������У����SqlBuilder.resolveSortField()���;Ĭ��create_time DESC;mvn compileͨ�� | (���ύ) |
| P0-001-006-004-002-002 | ʵ�ֲ�ѯ�߼� | 2026-05-30T03:32 | ? | SysDataViewController.java(@RestController /api/system/data-views)����CRUD+POST/{viewCode}/execute+GET/{viewCode}/meta+���з���@RequirePermission+execute����@OperLog+@Operation��ȫ;OperLog.javaע�ⶨ��;mvn compile BUILD SUCCESS | (���ύ) |
| P0-001-006-004-001-001 | ����ӿ�·���뷽��ǩ�� | 2026-05-30T04:00 | ? | SysDataViewController.java����P0-001-006-004-002-002����,��������֤:CRUD�ӿ�·��(/api/system/data-views)+7������ȫ��@RequirePermission+@Operation��ȫ+@OperLog,�������ձ�׼,mvn compileͨ�� | 6b6ffdca |
| P0-001-006-004-002-001 | ����ӿ�·���뷽��ǩ�� | 2026-05-30T05:00 | ? | SysDataViewController.java execute+getMeta�ӿ�·���뷽��ǩ���Ѿ�λ,POST /{viewCode}/execute(@OperLog)+GET /{viewCode}/meta,@RequirePermission+@Operation��ȫ,RESTful�淶,mvn compileͨ�� | 9aba4bdb |
| P0-001-006-003-003-003 | ��֤��ҳ���� | 2026-05-30T05:50 | ? | DataViewPagingExecutor.java�����ֶΰ�����У��(resolveSortField/sortOrder)+DataViewPagingExecutorVerificationTest.java(24����):SQL������2��+�ֶ�ת��1��+��ҳ����6��+�����ֶΰ�����4��+������3��+�쳣3��+�߽�4��,mvn test 92ͨ�� | (���ύ) |
| P0-001-007-001-001-002 | ����������Լ�� | 2026-05-30T18:30 | ? | V3__create_sys_param.sql����COMMENT ON CONSTRAINT/INDEX 4��+�����嵥��Լ��˵��ע�Ϳ�,��֤:uk_category_key��tenant_id+���������ų���ɾ��+mvn compileͨ�� | 81f08a73 |
| P0-001-006-004-001-002 | ʵ�������޸�ɾ������ | 2026-05-30T19:00 | ? | SysDataViewController.java CREATE/UPDATE/DELETE�����Ѿ�λ(����P0-001-006-004-002-002ʵ��),@RequirePermission+@Operation��ȫ,�ӿ�·��/api/system/data-views����RESTful�淶,mvn compileͨ�� | (��֤����) |
| P0-001-006-004-001-003 | ʵ�ֲ�ѯ���� | 2026-05-30T20:00 | ? | SysDataViewController.java��ѯ����ȫ����λ:POST/{viewCode}/execute(@OperLog)+GET/{viewCode}/meta+getById+pageList,SysDataViewServiceImpl.executeView+getViewMeta����ʵ��,@RequirePermission+@Operation��ȫ,mvn compileͨ�� | 3589b755 |
| P0-001-007-001-002-002 | ִ��DDL�ű� | 2026-05-30T21:00 | ? | ��֤V3__create_sys_param.sql����:CREATE TABLE sys_param(9ҵ���ֶ�+10ͨ���ֶ�)+Ψһ����uk_category_key(��tenant_id��������)+idx_category+idx_sys_param_tenant+ȫ��COMMENT+�ع��ű�,mvn compile BUILD SUCCESS | 1d61228f |
| P0-001-007-001-002-003 | ��֤������Լ�� | 2026-05-30T21:30 | ? | ��erp_dev��ִ��V3__create_sys_param.sql:sys_param��17�ֶ�+3����(pk_sys_param/uk_category_key/idx_category/idx_sys_param_tenant)+1����Լ��ȫ�������ɹ�,information_schema��֤ͨ��,�ֶ�������淶һ�� | 4e879d1e |
| P0-001-007-002-001-002 | ʵ��ServiceImpl | 2026-05-30T22:00 | ? | SysParamService.java(@Service):getValue(��������ת��)+getStr+setParam(UPSERT)+deleteParam(ϵͳ��������)+listByCategory,���ж�����@Cacheable(sys:param)+д����@Caching(@CacheEvict����������б�����),@Transactionalд����+JdbcTemplate+tenantId����+convertValue֧��5������(STRING/NUMBER/BOOLEAN/JSON/DATE),@EnableCaching���ӵ�ErpAiApplication,mvn compileͨ�� | 483c810e |
| P0-001-007-002-001-003 | ҵ��У���߼� | 2026-05-30T22:40 | ? | SysParamService.java����ҵ��У��:validateParamKey(category/key����Ϊ��)+getValue����type�ǿ�У��+setParam����value�ǿ�У��+listByCategory����category�ǿ�У��+deleteParamϵͳ��������(����),����public������ڲ���У�鸲��,@Transactional��public����,mvn compileͨ�� | 5ee6f0d9 |
| P0-001-007-002-001-001 | ����Service�ӿ� | 2026-05-30T23:00 | ? | SysParamService.java(@Service)�ӿڶ���:getValue���ͷ���+getStr+setParam+deleteParam+listByCategory,���ж�����@Cacheable(sys:param������������),д����@Caching(@CacheEvict����+�б�����),convertValue֧��5������ת��(STRING/NUMBER/BOOLEAN/JSON/DATE),validateParamKey����У��,mvn compileͨ�� | 0c57c72f |
| P0-001-007-001-002-001 | ��дCREATE INDEX��� | 2026-05-30T23:45 | ? | ��֤V3__create_sys_param.sql����ǰ�����񴴽�����:CREATE TABLE+3������(uk_category_key/idx_category/idx_sys_param_tenant)+ȫ��COMMENT+�ع��ű�,mvn compile BUILD SUCCESS | (���ύ) |
| P0-001-007-002-003-001 | ����ӿ�·���뷽��ǩ�� | 2026-05-30 | ? | ����ParamCacheRefresher.java(@EventListener����ParamChangedEvent+Redis�������+POST /api/system/params/refresh�ֶ�ˢ��)+ParamChangedEvent.java(�Զ����¼�),֧�ֶ�ʵ�����𻺴�ͬ��,mvn clean compile BUILD SUCCESS | b2e4335e |
| P0-001-007-002-003-002 | ʵ�ֽӿ��߼� | 2026-05-30T06:00 | ? | ParamCacheRefresher.java����ʵ��:@EventListener����+evictCache˽�з���+POST /refresh�ֶ�ˢ�¶˵�+StringRedisTemplate��ɨ�����,����ͨ�� | b2e4335e |
| P0-001-007-003-001-001 | ����ӿ�·���뷽��ǩ�� | 2026-05-30T07:30 | ? | SysParamController.java(@RestController /api/system/params):5��CRUD�˵�(listByCategory/getByCategoryAndKey/create/update/delete),ȫ��@RequirePermission+@Operation��ȫ,RESTful·���淶,is_system=1ɾ��������Service��ʵ��,/refresh�˵���ParamCacheRefresher���� | 8966b471 |
| P0-001-007-002-002-002 | ʵ�ָ߼�ת������ | 2026-05-30T18:30 | ? | ParamTypeConverter.java�����(commit 173e887b):convert()����֧��STRING/NUMBER/BOOLEAN/JSON/DATE 5������,DATE˫��ʽ����,convertNumber֧��Integer/Long/Double/Float/BigDecimal,convertBoolean֧��true/1/yes,ʧ����ParamException(30002),mvn compileͨ�� | 173e887b |
| P0-001-007-003-001-002 | ʵ�ֲ�ѯ�߼� | 2026-05-30T08:00 | ? | SysParamController.java����POST /refresh�˵�(StringRedisTemplate��ɨ�����sys:param:*����)+@RequirePermission(system:param:manage),ParamCacheRefresher�ع�Ϊ@Component(����@EventListener�Ƴ�@RestController),ȫ��6�˵�@RequirePermission+@Operation��ȫ,mvn compileͨ�� | 59ff6485 |
| P0-001-007-003-002-001 | ����ӿ�·���뷽��ǩ�� | 2026-05-30T08:30 | ? | SysParamController.java����PUT /batch�������¶˵�(@RequirePermission(system:param:update)+@Operation),����List<Map<String,String>>����,RESTful·��/api/system/params/batch,mvn compile BUILD SUCCESS | fa9d0b16 |
| P0-001-007-003-002-002 | ʵ�ֽӿ��߼� | 2026-05-30T09:00 | ? | SysParamController.java batchUpdate������stubʵ��Ϊ����ҵ���߼�:�������б�У��(BusinessException PARAM_MISSING)+ѭ������sysParamService.setParam��������+��־��¼,mvn compile BUILD SUCCESS | 377206c8 |
| P0-001-008-001-001-001 | ʵ���ļ��ϴ������߼� | 2026-05-30T10:00 | ? | FileUploadService.java(@Service):upload(MultipartFile)ʵ��MIMEħ�����(JPEG/PNG/PDF/XLS/OOXML)+��չ��������(exe/bat/sh/cmd)+UUID�ļ���+���ڷ�Ŀ¼(yyyy/MM/dd)�洢,FileVO+FileUploadProperties֧����,mvn compileͨ�� | a99f46bd |
| P0-001-008-001-001-002 | ʵ���ļ��ϴ������߼� | 2026-05-30T11:00 | ? | FileUploadService��ǿ:��ʽ�ļ���СУ��(���ļ�10MB PARAM_RANGE_ERROR)+SysFile Entity(@TableName sys_file)+SysFileMapper(BaseMapperX)+�ϴ���ɺ��Զ���¼sys_fileԪ����(�ļ���/·��/MIME/��С/CONFIRMED״̬/�ϴ���StpUtil��ȡ),mvn compileͨ�� | 24a8ec13 |
| P0-001-008-001-001-003 | ��֤�ļ��ϴ� | 2026-05-30T15:00 | ? | FileUploadServiceVerificationTest(17��ȫPASS):��չ��������(exe/bat/sh/cmd)5��+MIMEħ��������(JPEG/PNG/PDF)4��+�ļ���СУ��(��/null/����/�߽�)4��+�ɹ��ϴ�����(UUID��ʽ/����Ŀ¼/FileVO�����ֶ�/sys_fileԪ����)4��;�޸�BusinessException args����ܾ�Ĭ��������,mvn compile+test 171/171 PASS | (pending) |
| P0-001-008-001-002-001 | ʵ���ļ������߼� | 2026-05-30T07:05 | ? | FileDownloadService.java(@Service):download(fileId,response)��sys_fileԪ����+У���ļ����ڿɶ�+Content-Type(MIME)/Content-Disposition(attachment)/Content-Length��Ӧͷ+StreamingResponseBody��ʽ���(8KB�����OOM)+Range�ϵ�����(206 Partial Content/RandomAccessFile seek)+���ؼ�������,mvn compile BUILD SUCCESS | (pending) |
| P0-001-008-001-002-002 | ʵ���ļ����غ����߼� | 2026-05-30T07:08 | ? | FileDownloadService.handleFullDownload�ع�ΪBufferedInputStreamֱ����ʽ���(�Ƴ�����StreamingResponseBodyͬ����װ),handleRangeDownload����RandomAccessFile�ϵ�����,mvn compile BUILD SUCCESS | (pending) |
| P0-001-008-001-002-003 | ��֤�ļ����� | 2026-05-30T07:15 | ? | FileDownloadServiceVerificationTest(16��ȫPASS):����У��(�Ƿ�fileId)+�ļ�������(DB/����)+ȫ������(Content-Type/Content-Disposition/Content-Length/����һ����/��������/null MIME/�����ļ���)+Range�ϵ�����(206/Content-Range/��������/416 unsatisfiable/start-only),mvn test 16/16 PASS | 81d2e1d8 |
| P0-001-008-001-003-001 | ʵ���ļ�Ԥ���߼� | 2026-05-30T08:57 | ? | FilePreviewService.java(@Service):preview(fileId,response)��sys_fileԪ����+ͼƬ(image/*)ֱ����ʽ���+PDF/�ı�(text/*)ֱ��������ʽ+Office(msword/excel/powerpoint/officedocument)����JSON������ʾ+��֧�ָ�ʽ����JSON��������+Content-Disposition:inline(���������չʾ)+streamFile(8KB�����OOM),mvn compile BUILD SUCCESS | 679a805d |
| P0-001-008-001-003-002 | ʵ���ļ�Ԥ�������߼� | 2026-05-30T09:30 | ? | FilePreviewService.java�����߼���ǿ:handleTextPreview��Ϊ��ʽ���(streamFile+8KB����)������ļ�OOM,Content-Disposition:inline+Content-Length��ȷ����,�Ƴ�δʹ��import(RequestContextHolder/ServletRequestAttributes/StringJoiner),mvn compileͨ�� | 0df94857 |
| P0-001-008-001-003-003 | ��֤�ļ�Ԥ�� | 2026-05-30T09:07 | ? | FilePreviewServiceVerificationTest(22��ȫPASS):����У��2��+�ļ�������2��+ͼƬԤ��4��(Content-Disposition:inline/Content-Type/����һ����/Content-Length)+PDFԤ��3��+TXTԤ��3��(UTF-8�ı�)+OfficeԤ��3��(DOC/XLS/PPT����JSON��������)+��֧�ָ�ʽ2��+�����ļ���1��+�����ĵ�2��(JSON/HTML);�޸�text/plain��INLINE_DOC_TYPES�Ƴ�ʹ����handleTextPreview����charset=UTF-8 | f34f2922 |
| P0-001-008-002-001-002 | ʵ��ע�⴦���� | 2026-05-30T12:30 | ? | OperLog.javaע�ⶨ������:@Target(METHOD)/@Retention(RUNTIME)/@Documented,6����(module/action/description/saveRequestData/saveResponseData/isSaveErrorTrace)Ĭ��ֵ��ȷ,���OperLogAspectʹ��,mvn compileͨ�� | 9002503a |
| P0-001-008-002-002-001 | ��������������ע�᷽ʽ | 2026-05-30T17:24 | ? | OperLogAspect.java(@Aspect @Component @Slf4j):@Around(@annotation(OperLog))��¼������(StpUtil.getLoginId)/IP(X-Forwarded-For+RemoteAddr)/HTTP����/URL/��ʱ/�ɹ�ʧ��/�쳣��ջ��ȡ2000�ַ�,finally���첽����SysOperLogService.save();SysOperLogʵ��+SysOperLogService�ӿ�+@EnableAsync;mvn compile BUILD SUCCESS | 51ffa9fe |
| P0-001-008-002-002-002 | ʵ�ֺ��Ĵ����߼� | 2026-05-30T17:45 | ? | SysOperLogMapper.java(BaseMapperX<SysOperLog>)+SysOperLogServiceImpl.java(@Async @Service:�첽insert��sys_oper_log��),OperLogAspect�����߼�����(��¼������/IP/HTTP����/URL/��ʱ/�ɹ�ʧ��/�쳣��ջ��ȡ2000�ַ�/finally�����@Async save),mvn compile BUILD SUCCESS | fda70d58 |
| P0-001-008-002-002-003 | ���ɲ�����֤ | 2026-05-30T18:00 | ? | ��֤OperLogAspect��������:@Aspect @Component @Around����/@Async�첽����/@EnableAsync������/������(StpUtil)/IP(X-Forwarded-For��RemoteAddr)/HTTP����/URL/��ʱ/�ɹ�ʧ��/�쳣��ջ��ȡ2000�ַ�/SysOperLog���ֶ�ӳ����ȷ,mvn compile BUILD SUCCESS | 6cf9dc64 |
| P0-001-008-002-003-001 | ����ӿ�·���뷽��ǩ�� | 2026-05-30T18:15 | ? | SysOperLogController.java(@RestController @RequestMapping /api/system/oper-logs):GET/page��ҳ(operatorId/module/startTime/endTime/operatorIp/create_time DESC)+GET/{id}����+DELETE/clean���+GET/export����,ȫ��@RequirePermission(system:oper-log:query),mvn compileͨ�� | (pending) |
| P0-001-008-002-003-002 | ʵ�ֲ�ѯ�߼� | 2026-05-30 | ? | SysOperLogController.java�ع�ʹ��SysOperLogService(���ֱ��ע��Mapper),SysOperLogService����pageList/getById/clean/exportList����,SysOperLogServiceImplʵ��ȫ����ѯ����(buildQueryWrapper��ȡ������������),mvn compileͨ�� | (pending) |
| P0-001-008-003-001-001 | ʵ�ֵ����߼� | 2026-05-30T10:06 | ? | ExcelExportUtil.java:����export(response,fileName,clazz,data)����,����EasyExcel 3.3.3,����д��(BATCH_SIZE=5000),����MAX_ROWS=10000����,URLEncoder�ļ�������,Content-Type=application/vnd.ms-excel,�쳣throw BusinessException��GlobalExceptionHandler��RT.fail,pom.xml����easyexcel����,mvn compile BUILD SUCCESS | (pending) |
| P0-001-008-003-001-002 | �����쳣���� | 2026-05-30 | ? | ExcelExportUtil.java��ǿ�쳣����:�������쳣catch Exception��reset response��writeErrorResponseдRT.fail JSON(Content-Type��Ϊapplication/json),����Excel��Ӧͷ��JSON�����岻ƥ��;ǰ��У����throw BusinessException��GlobalExceptionHandler����,mvn compile BUILD SUCCESS | (pending) |
| P0-001-008-003-002-001 | ʵ�ֵ������� | 2026-05-30T12:20 | ? | ExcelImportUtil.java:����importExcel(MultipartFile,Class<T>)����,EasyExcel.read().sheet().doRead()ͬ����ȡ,ImportReadListener(AnalysisEventListener)�����ռ�����+onException�����������,MAX_ROWS=10000����,BusinessException�����ܾ�,ImportResult(successList+errorList with row+reason),�����в���ϵ���;ImportResult.java֧����,mvn clean compile 126Դ�ļ�BUILD SUCCESS | (pending) |
| P0-001-008-003-002-002 | ʵ�ֵ��빦�� | 2026-05-30T13:00 | ? | ExcelImportUtil.java����ʵ��:importExcel()���ͷ���+EasyExcel.read().sheet().doRead()+ImportReadListener�����ռ�+MAX_ROWS=10000+ImportResult(successList+errorList�к�+ԭ��)+�����в����+onException������ | 79ae6ef1 |
| P0-001-008-003-003-001 | ���嵼��ӿ�·�� | 2026-05-30T10:30 | ? | ImportTemplateController.java(@RestController /api/common/templates):GET /{module}/download��ģ��+GET /{module}/sampleʾ������,@RequirePermission(common:template:download),EasyExcel.write()����ģ�庬@ExcelProperty��ͷ+ʾ����,Content-Type=application/vnd.ms-excel,mvn compile BUILD SUCCESS | (pending) |
| P0-001-008-003-003-002 | ʵ�ֵ����߼� | 2026-05-30T11:00 | ? | downloadTemplate��ǿ:��ʾ��������(��ģ�庬@ExcelProperty��ͷ+ʾ��������),�����쳣����,��downloadSample�߼�����,mvn compileͨ�� | efbd5d20 |
| P0-001-008-004-001-001 | ���庯��ǩ�������� | 2026-05-30T14:00 | ? | TreeNode<T>(id/parentId/data/children+isLeaf/isRoot)+TreeUtil(final+private����):buildTree(2������Ĭ��rootParentId=0)+buildChildren�ݹ�+flattenչƽ+filterByPermission����,����ǩ������,mvn compileͨ�� | (pending) |
| P0-001-008-004-001-002 | ʵ�ֺ��Ĵ����߼� | 2026-05-30T15:00 | ? | buildTree(O(n) LinkedHashMap����)+buildChildren(�ݹ�Map����)+flatten(DFSչƽ)+filterByPermission(�ӽڵ�����),���з���null/empty��ȫ,mvn compileͨ�� | 737cbd36 |
| P0-001-009-001-001-001 | ��дCREATE INDEX��� | 2026-05-30T10:55 | ? | V4__create_doc_detail_location.sql(DDL��5����)+DocDetailLocation.java(extends BaseEntity)+DocDetailLocationMapper.java(extends BaseMapperX+selectByDetailId),mvn compileͨ�� | (pending) |
| P0-001-009-001-001-002 | ��дALTER TABLE ADD CONSTRAINT��� | 2026-05-30T11:02 | ? | V4��ǿ:ALTER TABLE ADD CONSTRAINT chk_ddl_quantity(quantity>=0)+chk_ddl_is_default(is_default IN 0/1)+COMMENT ON CONSTRAINT+�߼����˵��,mvn compileͨ�� | 4cc25117 |
| P0-001-009-001-002-001 | ����Mapper�ӿ� | 2026-05-30T11:10 | ? | DocDetailLocationMapper extends BaseMapperX,selectByDetailId����,@Mapperע��,��������ǰ�����񴴽�������ͨ�� | 87ee7b44 |
| P0-001-009-001-002-002 | ��дXMLӳ���ļ� | 2026-05-30T11:20 | ? | DocDetailLocationMapper.xml:BaseResultMapȫ�ֶ�ӳ��+Base_Column_List SQLƬ��+selectByDetailId�Զ����ѯ(WHERE is_deleted=FALSE),mvn compileͨ�� | 25ac9932 |
| P0-001-009-002-001-001 | ��дCREATE INDEX��� | 2026-05-30T11:55 | ? | V5__create_doc_detail_batch.sql(DDL��6����)+DocDetailBatch.java(extends BaseEntity)+DocDetailBatchMapper.java(extends BaseMapperX+selectByDetailId),mvn compileͨ�� | e20f4fd2 |
| P0-001-009-002-001-002 | ��дALTER TABLE ADD CONSTRAINT��� | 2026-05-30T12:00 | ? | V5��ǿ:ALTER TABLE ADD CONSTRAINT chk_ddb_quantity(quantity>=0)+chk_ddb_date(expiry_date>=production_date��NULL)+COMMENT ON CONSTRAINT+�߼����˵��,mvn compileͨ�� | 1e17fb32 |
| P0-001-009-002-002-001 | ����Mapper�ӿ� | 2026-05-30T12:15 | ? | DocDetailBatchMapper extends BaseMapperX,selectByDetailId,@Mapperע��,��������ǰ������(e20f4fd2)�д���������ͨ�� | (pending) |
| P0-001-009-002-002-002 | ��дXMLӳ���ļ� | 2026-05-30T12:30 | ? | DocDetailBatchMapper.xml:BaseResultMapȫ�ֶ�ӳ��(16�ֶ�)+Base_Column_List SQLƬ��+selectByDetailId�Զ����ѯ(WHERE is_deleted=FALSE),mvn compileͨ�� | (pending) |
| P0-001-009-003-001-001 | ��дCREATE INDEX��� | 2026-05-30T12:16 | ? | V6__create_doc_detail_serial.sql(DDL��5����)+DocDetailSerial.java(extends BaseEntity+4ҵ���ֶ�)+DocDetailSerialMapper.java(extends BaseMapperX+selectByDetailId),mvn compileͨ�� | 488691eb |
| P0-001-009-003-001-002 | ��дALTER TABLE ADD CONSTRAINT��� | 2026-05-30T12:20 | ? | V6��ǿ:ALTER TABLE ADD CONSTRAINT chk_dds_status(status BETWEEN 1 AND 3)+chk_dds_serial_no(serial_no�ǿ�)+COMMENT ON CONSTRAINT+�߼����˵��,mvn compileͨ�� | 8cf169a8 |
| P0-001-009-003-002-001 | ����Mapper�ӿ� | 2026-05-30T12:35 | ? | DocDetailSerialMapper extends BaseMapperX<DocDetailSerial>,����selectByDetailId�Զ����ѯ����,mvn compileͨ�� | (pending) |
| P0-001-009-003-002-002 | ��дXMLӳ���ļ� | 2026-05-30T12:27 | ? | DocDetailSerialMapper.xml:BaseResultMapȫ�ֶ�ӳ��(14�ֶ�)+Base_Column_List SQLƬ��+selectByDetailId�Զ����ѯ(WHERE is_deleted=FALSE),mvn compile BUILD SUCCESS | 5cb4a7cf |
| P0-001-009-004-001-001 | ����Service�ӿ� | 2026-05-30T12:38 | ? | DetailSubTableService<D,L,B,S>�����ͻ���:saveSubTables(@Transactional��ɾ����)+deleteByDetailId(QueryWrapper����ɾ��)+getByDetailId(��������)+validateInventoryQuantity���У��+DetailSubTableDTO�ӱ�����,mvn compile BUILD SUCCESS | 3631ee5f |
| P0-001-009-004-001-002 | ʵ��ServiceImpl | 2026-05-30T13:00 | ? | DetailSubTableServiceʵ������֤:saveSubTables/deleteByDetailId/getByDetailId������ȫ��ʵ��,validateInventoryQuantity���У�鹳��,mvn compile BUILD SUCCESS | |
| P0-001-009-004-001-003 | ҵ�������� | 2026-05-30T13:18 | ? | DetailSubTableServiceҵ��������:selectLocationsByDetailId/selectBatchesByDetailId/selectSerialsByDetailId����������ѯ+deleteLocationsByDetailId/deleteBatchesByDetailId/deleteSerialsByDetailId��������ɾ��+validateInventoryQuantity�������SUM�ۺ�У��+isSubTableDataEmpty�����ж�,mvn compile BUILD SUCCESS | 05e7182f |

### ģ�����: P0-001 ?

### P0-002 - ǰ����Ŀ��ܴ

| ������ | �������� | ���ʱ�� | ״̬ | ժҪ | Git SHA |
|---------|---------|---------|:---:|------|---------|
| P0-002-001-001-001-002 | ��֤��Ŀ������ | 2026-05-30 | ? | ��֤��Ŀ������:pnpm install(48����װ�ɹ�)+pnpm dev(VITE v8.0.14 localhost:5173)+pnpm build(vue-tsc+vite 18ģ�鹹��,dist��index.html+assets/.js/.css)+pnpm preview(localhost:4173����),ȫ5������ͨ�� | 52664d4e |
| P0-002-001-001-002-001 | ��װESLint+Prettier����+��д���� | 2026-05-30 | ? | ��װeslint@8.57.1+prettier@3.8.3+���,����.eslintrc.cjs/.prettierrc/.eslintignore/.prettierignore,pnpm lint�ޱ���,pnpm format��ʽ������,pnpm buildͨ�� | a9668d33 |
| P0-002-001-001-002-002 | ����VSCode���� | 2026-05-30 | ? | ����.vscode/settings.json(formatOnSave+defaultFormatter Prettier+codeActionsOnSave ESLint+���ļ����͸�ʽ����)+����.vscode/extensions.json(�Ƽ�Volar+ESLint+Prettier) | d0cd6ea2 |
| P0-002-001-001-002-003 | ��֤����淶 | 2026-05-30 | ? | ��֤ESLint/Prettier/VSCode������������:pnpm lint�˳���0,pnpm format��ʽ��src/�ļ�����,ESLint���var������no-var,VSCode�����Զ��޸�,.eslintignore����dist/.prettierignore����pnpm-lock.yaml,pnpm buildͨ��(18ģ��145ms) | (��֤����) |
| P0-002-001-001-003-001 | ��װHusky��lint-staged����pre-commit�ű� | 2026-05-30 | ? | pnpm add -D husky@9.1.7+lint-staged@17.0.5,��ʼ��.husky/pre-commit(cd erp-ai-web && npx lint-staged),����lint-staged(*.{vue,ts,tsx}:eslint+prettier,*.{css,scss}:prettier,*.{json,md}:prettier),��֤���ز��淶�����ύ | 01bc3293 |
| P0-002-001-001-003-002 | ����commitlint | 2026-05-30 | ? | ��װ@commitlint/cli@21.0.2+@commitlint/config-conventional@21.0.2,����commitlint.config.js(10��type CJS��ʽ)+.husky/commit-msg(pnpm exec commitlint --edit "$1"),��֤���淶�ύ���ܾ��淶�ύͨ�� | a79a2851 |
| P0-002-001-001-003-003 | ��֤Git���� | 2026-05-30 | ? | ��֤Husky+lint-staged+commitlint��������:pre-commit���ز��淶����(ESLint����),commit-msg�ܾ����淶�ύ��Ϣ(type-empty),�淶����+�淶message�����ύ,lint-staged������ݴ��ļ�,��������<1��,ȫ8������ͨ�� | 26de093f |
| P0-002-001-002-001-001 | ��װElement Plus�������ð������� | 2026-05-30 | ? | pnpm add element-plus+unplugin-vue-components+unplugin-auto-import,vite.config.ts����AutoImport(imports:vue/vue-router/pinia)+Components(ElementPlusResolver),.gitignore����dts�����ļ�,pnpm buildͨ��(129K<500KB) | 79f4a1e5 |
| P0-002-001-002-001-002 | �������ⶨ�� | 2026-05-30 | ? | ����src/styles/element-plus.scss����CSS����(Ʒ��ɫ/����/Բ��/���/����),vite.config.ts����ElementPlusResolver({importStyle:'sass'})+@·������,main.ts���������ļ�,tsconfig.app.json����pathsӳ��,��װsass����,pnpm buildͨ��(19ģ��315ms) | f239017e |
| P0-002-001-002-001-003 | ��֤�������� | 2026-05-30 | ? | ��֤Element Plus��������+���ⶨ����������:ElButton/ElInput����Զ�����(components.d.ts��ElButton/ElInput),ref/computed/ElMessage�Զ�����(auto-imports.d.ts��ȫ������),����ɫ#409EFF������Ч,pnpm buildͨ��(195KB<500KB),auto-imports.d.ts+components.d.ts�Զ�����,ȫ7������ͨ�� | 78d1341f |
| P0-002-001-002-002-001 | ���庯��ǩ�������� | 2026-05-30T14:05 | ? | ����src/plugins/vxe-table.ts(setupVxeTable����+VxeI18nConfig/VxeDefaultConfig�ӿ�+VxeTablePlugin����)+src/types/global.d.ts(VxeTablePropTypes��չ),pnpm add vxe-table@4.19.4+vxe-pc-ui@4.14.25,vue-tsc --noEmitͨ�� | 433c4cdf |
| P0-002-001-002-002-002 | ʵ�ֺ��Ĵ����߼� | 2026-05-30T14:18 | ? | pnpm add @vxe-ui/core,vxe-table.ts:VxeUI.setup()ȫ��Ĭ�ϲ���(border/resizable/showOverflow/autoResize/emptyText+pager),main.ts:����vxe-pc-ui/vxe-table��ʽ+setupVxeTable(app),pnpm buildͨ��(530ģ��582ms) | fa9edfa4 |
| P0-002-001-002-003-001 | ����ʵ����������� | 2026-05-30T14:30 | ? | pnpm add pinia axios nprogress @vueuse/core dayjs echarts lodash-es pinia-plugin-persistedstate + @types/nprogress @types/lodash-es(-D),����src/stores/index.ts(Piniaʵ��+persistedstate���),����src/utils/request.ts(Axiosʵ��baseURL/env+timeout 15s+�������Ǽ�),pnpm buildͨ�� | e990ab57 |
| P0-002-001-002-003-002 | ��Ӧ�����߼� | 2026-05-30T14:45 | ? | ����request.ts��Ӧ������:code===0����data��������װ/401����Tokenˢ��(��������isRefreshing+refreshSubscribers����)/403Ȩ�޲�����ʾ/����ҵ�����ElMessage+Promise.reject/�������ʱHTTP״̬��������ʾӳ��,����onTokenRefreshed+subscribeTokenRefresh,pnpm buildͨ��(530ģ��599ms) | 1cac7a4c |
| P0-002-001-002-003-003 | ������� | 2026-05-30T15:00 | ? | ����request.ts���������:pendingMap+AbortController�ظ�����ȡ��(getRequestKeyͳһkey)/NProgressȫ��loading+requestCount��������/silent��Ĭģʽ/GET�����Զ�����(�������ʱ���3��1s���)/��Ӧ������������������·,pnpm buildͨ��(530ģ��602ms) | 1ef7ce64 |
| P0-002-001-003-001-001 | ��дvite.config.ts | 2026-05-30T15:20 | ? | ����vite.config.ts:plugins(vue/vueJsx/AutoImport/Components)+resolve.alias(@/@components)+server(port:5173/proxy/api��localhost:8080/changeOrigin)+build(target:es2015/outDir:dist/chunkSizeWarningLimit:1500/manualChunks�ְ�vendor/vue/element-plus),pnpm add @vitejs/plugin-vue-jsx,tsconfig.app.json����@components/*·��ӳ��,pnpm buildͨ��(530ģ��580ms) | 0c9a8c40 |
| P0-002-001-003-001-002 | ��֤Vite���� | 2026-05-30T15:35 | ? | ��֤vite.config.ts��������:resolve.alias(tsconfig·��ӳ��һ��+main.tsʹ��@/����������ͨ��)+proxy(/api��localhost:8080+changeOrigin:true)+plugins(vue/vueJsx/AutoImport/Componentsȫ������)+pnpm build(530ģ��581ms+dist��index.html+assets)+server.port:5173+envPrefixĬ��VITE_+vite/client����������,ȫ4/6�������֤ͨ��(.env�ļ���һ���񴴽�) | |
| P0-002-001-003-001-003 | ��֤HMR�빹�� | 2026-05-30T17:19 | ? | ��֤HMR����������:pnpm build�ɹ�(exit 0,595ms,530ģ��)/dist����1.7MB<2MB/build.target:es2015��Ч(��?./??�﷨)/chunkSizeWarningLimit:1500����(���chunk 1087KB�޾���)/manualChunks�������(Ӧ�á�index,���vue chunk)/dev server����823ms(@vitejs/plugin-vue+HMRĬ������),ȫ7������ͨ�� | f50ea8ed |
| P0-002-001-003-002-001 | ��д.env.dev.env.prod.env.local | 2026-05-30T17:25 | ? | ����.env(VITE_APP_TITLE)/.env.development(VITE_APP_TITLE/API_BASE_URL/USE_MOCK/WS_URL)/.env.production(����ֵ)/.env.local(gitignore���ظ���),ȫ��VITE_ǰ׺ | da924e1b |
| P0-002-001-003-002-002 | �ڴ�����ʹ�û������� | 2026-05-30T17:40 | ? | ����src/env.d.ts(ImportMetaEnv��������VITE_APP_TITLE/API_BASE_URL/USE_MOCK/WS_URL)+main.ts(document.title��������+Mock����VITE_USE_MOCK��̬����)+index.html(%VITE_APP_TITLE%ռλ��)+src/mock/index.ts(setupMock stub),request.ts��ʹ��VITE_API_BASE_URL,pnpm buildͨ��(532ģ��551ms)
| P0-002-001-003-002-003 | ��֤�����л� | 2026-05-30T18:05 | ? | ��֤���������л�:.env.development/.env.production�ļ������ұ�����ȷ,git statusȷ��.env.localδ��׷��,dev����(--mode development)���ؿ�������(title=ERP-AI��������),prod����������������(title=ERP-AI),.env.local�������ȼ�����Vite�淶,��VITE_�������ɶ�ȡ(Vite������Ϊ),pnpm buildͨ��(530ģ��533ms) | 69affc3d |
| P0-002-001-004-001-001 | ��д.vueģ������ | 2026-05-30T18:15 | ? | ����src/types/shims-vue.d.ts:declare module '*.vue'ʹ��DefineComponent<{},{},any>����,tsc--noEmitͨ��,TypeScript��ȷʶ��.vue�ļ����� | |
| P0-002-001-004-001-002 | ��дenv.d.ts������������ | 2026-05-30T18:20 | ? | ����src/env.d.ts:ImportMetaEnv�ӿں�6��VITE_����(VITE_APP_TITLE/API_BASE_URL/USE_MOCK/APP_ENV/WS_URL/CDN_BASE_URL)+JSDocע��+readonly����,ImportMeta��չ,tsc--noEmit+vite buildͨ�� | 2d400ca3 |
| P0-002-001-004-001-003 | ��д��չ�������� | 2026-05-30T18:25 | ? | ����src/types/global.d.ts:����RecordObject<T>/PageQuery/PageResult<T>/ApiResult<T>�ĸ�ȫ������+NProgress Window��չ,tsc--noEmitͨ�� | 01f4ab78 |
| P0-002-002-001-001-001 | ����·�������� | 2026-05-30T17:59 | ? | ����erp-ai-web/src/router/modules/static.ts:6����̬·�ɳ���(LOGIN_ROUTE/ROOT_ROUTE/HOME_ROUTE/ERROR_404/ERROR_403/NO_PERMISSION)+staticRoutes����,������+meta��������,vue-tsc����ͨ�� | (pending) |
| P0-002-002-001-001-002 | ʵ��·��ע�������� | 2026-05-30T18:30 | ? | ����erp-ai-web/src/router/index.ts:createRouterʵ��(createWebHistory+staticRoutes+scrollBehavior+strict:true),pnpm add vue-router@4,router������main.tsʹ��,pnpm buildͨ��(532ģ��547ms) | c9d12117 |
| P0-002-002-001-002-001 | ����ǰ��·������ | 2026-05-30T18:35 | ? | ����erp-ai-web/src/router/types.ts:RouteMeta������չ(title/titleI18n/icon/keepAlive/hideMenu/hideTab/permissions/openType/affix);����erp-ai-web/src/router/modules/dynamic.ts:import.meta.glob���ӳ��+resolveComponent(��404����);router/index.ts����types.ts;�޸�ROOT_ROUTEȱʧtitle;pnpm buildͨ�� | 3bd8f97d |
| P0-002-002-001-002-002 | ���ú�˲˵����� | 2026-05-30T18:45 | ? | ����erp-ai-web/src/api/types/menu.ts:MenuItem�ӿ�(id/parentId/name/path/component/icon/sort/type/permissions/visible/keepAlive/openType/children)+MenuResponse�ӿ�(menus/permissions);����erp-ai-web/src/utils/menuTransform.ts:transformMenuToRoutes����(����type=2��ť/����/�ݹ�ת��/Ŀ¼��AppLayout/�˵���resolveComponent/���������޸�ԭʼ����);vue-tsc���ͼ��ͨ�� | adde8bf6

| P0-002-002-001-003-001 | ����·�������� | 2026-05-30T18:50 | ? | ����erp-ai-web/src/router/constants.ts(WHITE_LIST/LOGIN_PATH/HOME_PATH/NOT_FOUND_PATH/TOKEN_KEY/ROUTES_LOADED_KEY);����erp-ai-web/src/stores/modules/permission.ts(PermissionState/routes/isRoutesLoaded/permissions/hasPermission/resetPermission);vue-tsc����� | 1866bfb3 |
| P0-002-002-001-003-002 | ʵ��·��ע�������� | 2026-05-30T18:35 | ? | ����erp-ai-web/src/router/guards.ts:setupRouterGuards����(beforeEachȫ������:���������С�TokenУ�����̬·�ɼ��ء�Ȩ��У����������á�nextȫ��֧����);����router/index.ts���벢����setupRouterGuards;����stores/modules/user.ts��С����׮ | 73225336 |
| P0-002-002-002-001-001 | ʵ�ֲ��������ṹ | 2026-05-30T18:50 | ? | ����erp-ai-web/src/layouts/AppLayout.vue:el-containerǶ��(el-aside+el-container��el-header+tabs+el-main),aside��̬����(64px/220px),keep-alive include��cachedViews,transition fade-transform����,router-view key=fullPath;����stores/modules/layout.ts+tagsView.ts׮;����components/Sidebar/Navbar/TabNav׮ | (pending) |
| P0-002-002-002-001-002 | ʵ�ֲ���״̬���� | 2026-05-30T19:00 | ? | ��дerp-ai-web/src/stores/modules/layout.ts:Setup Store�﷨,isCollapsed(boolean)+localStorage�־û�(watch),isFullscreen(boolean)+Fullscreen API(async/await),toggleCollapse/toggleFullscreen actions,initFullscreenListener�¼�����,vite buildͨ�� | (pending) |
| P0-002-002-002-001-003 | ������ʽ�붯�� | 2026-05-30T19:05 | ? | ����erp-ai-web/src/layouts/styles/app-layout.scss(�����transition width 0.3s/sticky header z-index 100/min-width 1024px/overflow-y auto/fade-transform����)+AppLayout.vue������ʽ,vite buildͨ�� | (pending) |
| P0-002-002-002-002-001 | ���������ֽṹ | 2026-05-30T19:10 | ? | ��дNavbar.vue:flex����space-between(����۵���ťFold/Expandͼ���л�+���м,�Ҳ�ȫ������+��Ϣ����el-badge+�û�ͷ��el-dropdown trigger=click+ȫ���л�);����Breadcrumb.vue(route.matched���м);��дuser.ts(Setup Store+avatar/nickname computed+logout action+router.push);vue-tsc�����+vite buildͨ��(532ģ��559ms) | (pending) |
| P0-002-002-002-002-002 | ���������򽻻� | 2026-05-30T19:25 | ? | ����Navbar.vue(ElMessageBoxȷ���˳���¼+el-popover��Ϣ��������+SearchDialog���ref����);����SearchDialog.vue(Teleport to body+Ctrl+K��ݼ�+el-dialog��������);��װ@element-plus/icons-vue;�޸�menuTransform.ts���ʹ���;vue-tsc+vite buildͨ��(532ģ��546ms) | (pending) |
| P0-002-002-002-002-003 | ��������Ӧʽ���� | 2026-05-30T19:17 | ? | ����useResponsive.ts composable(768/1024�ϵ�+resize����+isMobile/isTablet);����navbar-responsive.scss(�ƶ���overlayģʽ+ƽ������);����AppLayout.vue(useResponsive����+overlay����+�ƶ����Զ��۵�);����app-layout.scss(�Ƴ�min-width+overlay base��ʽ);pnpm buildͨ�� | e879a367 |
| P0-002-002-002-003-001 | ����ǰ��·������ | 2026-05-30T19:30 | ? | ����erp-ai-web/src/layouts/components/Sidebar/types.ts(SidebarProps/MenuItemData�ӿ�+routeToMenuItem����);����Sidebar/index.vue(����SidebarProps+defineProps);����AppLayout.vue(����isCollapsed prop);vue-tsc+vite buildͨ��(532ģ��552ms) | (pending) |
| P0-002-002-002-003-002 | ���ú�˲˵����� | 2026-05-30T19:23 | ? | ����erp-ai-web/src/layouts/components/Sidebar/menuConfig.ts(filterMenuRoutes/filter hideMenu=true+DEFAULT_OPEN_MENUS+SIDEBAR_LOGO);vue-tsc --noEmit����� | 03bb1303 |
| P0-002-002-003-001-001 | ����Pinia store | 2026-05-30T19:35 | ? | ��дtagsView.tsΪSetup Store:TagView�ӿ�(8�ֶ�)/visitedViews+cachedViews ref/computed affixTags/addView/delView/delOtherViews/delAllViews/updateVisitedView;vue-tsc+vite buildͨ��(532ģ��553ms) | (pending) |
| P0-002-002-003-001-003 | ʵ���Ҽ��˵� | 2026-05-30T19:55 | ? | ����ContextMenu.vue:Teleport to body+6���˵���(ˢ��/�رյ�ǰ/�ر�����/�ر���/��/ȫ��)+�߽���+affix����+click outside�ر�+defineExpose;����TagView�ӿ�;vue-tsc+vite buildͨ�� | e17f54ed |
| P0-002-002-003-002-001 | �������propsemits | 2026-05-30T19:42 | ? | ����TabNav/types.ts(TabNavProps+TabNavEmits�ӿ�)+����TabNav/index.vue(defineProps activePath/views+defineEmits select/close/refresh/contextmenu)+����AppLayout.vue����props;vue-tsc+vite buildͨ�� | 027b5e53 |
| P0-002-002-003-002-002 | ʵ�����ģ��ṹ | 2026-05-30T20:15 | ? | TabNav/index.vue template+scoped��ʽ:�����������(overflow-x:auto+���ع�����)+v-for��ǩ�б�(key=fullPath)+active����(primaryɫ����)+affix��ǩ���عرհ�ť+@click.stop��ð��+ContextMenu����;vue-tsc+vite buildͨ�� | 12398a89 |
| P0-002-002-003-002-003 | ʵ������߼� | 2026-05-30T20:40 | ? | TabNav/index.vue�԰������:watch route.fullPath�Զ�addView+handleSelect·����ת+handleClose�رձ�ǩ+handleContextmenu�Ҽ��˵�+scrollToActiveTag�Զ�����+AppLayout.vue�Ƴ�props;pnpm buildͨ��(532ģ��533ms) | ba0c7a4a |
| P0-002-002-003-003-001 | ʵ��keep-alive������� | 2026-05-30T21:00 | ? | AppLayout.vue����:max="MAX_CACHED_VIEWS"(10)������󻺴���;tagsView.ts����MAX_CACHED_VIEWS����+LRU��̭�߼�(����shift������);vue-tscͨ�� | 465f2cec |
| P0-002-002-003-003-002 | ʵ�ֻ���ˢ�»��� | 2026-05-30T21:10 | ? | ����redirect·��+���(exclude��redirect��include����);�޸�refreshSelectedPage����;pnpm buildͨ��(532ģ��) | fdd2343b |
| P0-002-002-004-001-001 | ����ǰ��·������ | 2026-05-30T21:20 | ? | ����api/modules/menu.ts:getMenuList()����getInfo�ӿ�+flattenMenuTree()��ƽ��Ƕ�ײ˵���+extractPermissions()��ȡȨ�ޱ�ʶ;pnpm buildͨ�� | 5c5d6740 |
| P0-002-002-004-001-002 | ���ú�˲˵����� | 2026-05-30T21:35 | ? | ����utils/menuPipeline.ts:isExternalIcon/isSvgIcon/normalizePath/processMenuData/getCachedMenus/clearMenuCache;MenuItem����iconType�ֶ�;vue-tscͨ�� | 0d8af4a2 |
| P0-002-002-004-002-001-001 | ʵ�ֵݹ�˵���� | 2026-05-30T21:50 | ? | ����SidebarItem.vue�ݹ�˵����:defineOptions({name:'SidebarItem'})+visibleChildren computed����hideMenu+hasVisibleChildren�ж���Ⱦel-sub-menu��el-menu-item+v-for key=path;����MenuItemIcon.vue(stub);pnpm buildͨ��(532ģ��) | 0cb40b74 |
| P0-002-002-004-002-001-002 | ʵ�ֲ˵�ͼ��+���� | 2026-05-30T21:15 | ? | ��дMenuItemIcon.vue֧��3��ͼ������(element:ElPlus��̬���/svg:SvgIcon���/external:img��ǩ);����components/SvgIcon/index.vue(import.meta.glob eager����SVG raw�ַ���);����SidebarItem.vue��������(a��ǩtarget=_blank+rel=noopener noreferrer);����types.ts����iconType�ֶ�;pnpm buildͨ��(532ģ��) | (pending) |
| P0-002-002-004-002-001-003 | ʵ�ֲ˵�Ȩ�޹��� | 2026-05-30T21:45 | ? | ����utils/permission.ts(hasPermission��superadmin�����ж�+filterRoutesByPermission�ݹ���˴��������˵���������);����Sidebar/index.vue(computed filteredMenus����filterRoutesByPermission+routeToMenuItem);pnpm buildͨ�� | (pending) |
| P0-002-002-004-002-002 | ʵ�ֲ˵����� | 2026-05-30T22:00 | ? | ��дSidebar/index.vue:Logo����(SIDEBAR_LOGO�۵�/չ���л�+�������ҳ)+el-scrollbar�����˵�+handleMenuSelect(����window.open/��ͨrouter.push)+activeMenu computed(meta.activeMenu����+route.path)+layoutStore.isCollapsed����+unique-opened�ַ���+collapse-transition����;pnpm buildͨ�� | (pending) |
| P0-002-002-004-002-003 | ʵ�ֲ˵�Ȩ�޹��� | 2026-05-30T22:15 | ? | ����directives/permission.ts(v-permissionָ��mounted����removeChild�Ƴ���Ȩ��DOM)+directives/index.ts(setupDirectivesȫ��ע��)+main.tsע��pinia+setupDirectives;pnpm buildͨ�� | 250a3cda |
| P0-002-003-001-001-001 | ����State�������ʼֵ | 2026-05-30T22:00 | ? | ����types/user.d.ts(IUserState/UserInfoVO�ӿ�)+��дstores/modules/user.ts(Options API+state��ʼֵ+token�־û�erp_user+persist.pick['token']);pnpm buildͨ�� | 9dfe5339 |
| P0-002-003-001-001-002 | ʵ��Actions | 2026-05-30T22:00 | ? | ʵ��userStore��login(loginApi����token��getInfo)/getInfo(getUserInfoApi����userInfo+permissions+roles��ʧ���Զ�logout)/logout(���state+localStorage.removeItem+router.replace)����action,����api/modules/auth.ts+api/types/auth.ts(LoginDTO/LoginResponse/UserInfoResponse),����guards.ts����������,pnpm buildͨ�� | bbc1dbcf |
| P0-002-003-001-001-003 | ʵ��Getters | 2026-05-30T22:30 | ? | ʵ��userStore�ĸ�getter:isLoggedIn(!!state.token)/hasPermission(perm=>permissions.includes)/avatar(userInfo?.avatar||'/default-avatar.png')/nickname(nickname||username||'�û�'),pnpm buildͨ�� | 0401ac22 |
| P0-002-003-001-002-001 | ����State�������ʼֵ | 2026-05-30T22:50 | ? | ����types/app.d.ts(DeviceType/ThemeType/IAppState)+stores/modules/app.ts(defineStore Options API+5��state�ֶ�+persist�־û�erp_app),tsc --noEmitͨ�� | 065de044 |
| P0-002-003-001-002-002 | ʵ��Actions | 2026-05-30 | ? | ʵ��appStore 5��Actions:toggleSidebar(��תsidebarCollapsed+persist�Զ�����)/setDevice(����device+mobile�Զ��۵������)/setTheme(����state+document data-theme����+dark+el-dark class)/setLanguage(����state+ͬ��i18n locale lazy ref)/setActiveMenu(��¼��ǰ·��),pnpm buildͨ�� | (pending) |
| P0-002-003-001-002-003 | ʵ��Getters | 2026-05-30 | ? | ʵ��appStore 4��Getters:isMobile(state.device==='mobile'��boolean)/sidebarStatus(sidebarCollapsed?'closed':'opened')/currentTheme(state.theme��ThemeType)/locale(state.language��string),����getter���Ͱ�ȫ��DOM����,pnpm buildͨ�� | 32c2d901 |
| P0-002-003-001-003-001 | ����ǰ��·������ | 2026-05-30T22:10 | ? | router/index.ts(Routerʵ��+scrollBehavior)+modules/static.ts(staticRoutes:LOGIN/404/403/ROOT/HOME)+modules/dynamic.ts(import.meta.glob��̬����resolveComponent)+types.ts(RouteMeta��չactiveMenu);vue-tsc+vite buildͨ�� | 21d87b7c |
| P0-002-003-001-003-002 | ���ú�˲˵����� | 2026-05-30T22:35 | ? | FlywayǨ��V7__init_menu.sql:CREATE TABLE sys_menu(12ҵ���ֶ�+ͨ���ֶ�)+����3��+INSERT�˵�����(ϵͳ����/�û�����/��ɫ����/�˵�����/���Ź���/��λ����/�ֵ����/��֯�ܹ�/��˾����/��������/�������/ϵͳ����)�������νṹ | (pending) |
| P0-002-003-001-004-001 | ����State�������ʼֵ | 2026-05-30T23:00 | ? | ����types/dict.d.ts(DictItem+IParamState�ӿ�)+stores/modules/param.ts(defineStore Options API+3��state�ֶ�+��persist�־û�);tsc --noEmitͨ�� | 2d0561a0 |
| P0-002-003-001-004-002 | ʵ��Actions | 2026-05-30T22:10 | ? | paramStoreʵ��loadDict/refreshDict/loadSystemConfig����action+pendingPromises Map���ظ�����+����api/modules/system.ts(getDictDataApi/getSystemConfigApi);pnpm buildͨ�� | e5476d33 |
| P0-002-003-001-004-003 | ʵ��Getters | 2026-05-30T22:15 | ? | paramStoreʵ������getters:getDictByType/getDictLabel/getConfig,����ʽ��ͷ�﷨,��ֵ���׷��ؿ�����/���ַ���,pnpm buildͨ�� | d2e50675 |
| P0-002-003-002-001-002 | ʵ�ֺ��Ĵ����߼� | 2026-05-30T22:45 | ? | ����request.ts:��չInternalAxiosRequestConfig��������metadata�ֶ�(requestKey/startTime/skipCancel)+��������������requestKey(method:url:params:data��ʽ+JSON.stringify��ֵ����)+��ֵconfig.metadata,npx tsc --noEmitͨ�� | 6b432623 |
| P0-002-003-002-001-003 | ���ɲ�����֤ | 2026-05-30T22:50 | ? | ��֤Axiosʵ��7��ȫ��ͨ��:baseURL����(`/api`)/timeout(30000)/Tokenע��(Authorization:Bearer)/����ע��(Accept-Language:zh-CN)/���ݽ��(code===0��data)/������ʾ(ElMessage.error)/TypeScript�����(tsc --noEmit) | |
| P0-002-003-002-002-001 | ��������������ע�᷽ʽ | 2026-05-30T23:10 | ? | ����types/api.d.ts(ApiResponse<T>/PageResult<T>���Ͷ���)+����request.ts��Ӧ������(code===0����data/code===20001 Token����/code===40001/40003Ȩ�޲���)+AxiosResponse<ApiResponse>���ͱ�ע+tsc--noEmit����ͨ�� | |
| P0-002-003-002-002-003 | ���ɲ�����֤ | 2026-05-30T23:45 | ? | ��̬��֤��Ӧ������8���嵥ȫ��ͨ��(code===0���/20001ˢ��/40001Ȩ��/HTTP 401-500����/��ʱ/�����쳣)+pnpm build����ͨ�� | f38ca9ee |
| P0-002-003-002-003-001 | ʵ��401������Tokenˢ������ | 2026-05-30T22:50 | ? | handleTokenRefresh���ĺ���:isRefreshing��������+refreshAxios����ʵ��������ѭ��+pendingRequests���й���+refreshʧ�ܵ���logout��ת��¼ҳ+pnpm build����ͨ�� | fabcaddb |
| P0-002-003-002-003-002-001 | ʵ�������Ŷӻ��� | 2026-05-30T23:00 | ? | PendingRequest�ӿڶ���+pendingQueue����+addToQueue(Promise����+30s��ʱ����+��ʱ�Ӷ����Ƴ�)+replayRequests/handleRefreshFailure���������ݽṹ+tsc --noEmitͨ�� | ceec0f6c |
| P0-002-003-002-003-002-003 | ʵ��ˢ��ʧ�ܴ��� | 2026-05-31 | ? | handleRefreshFailure��д:reject�����Ŷ�����(��¼�ѹ���)+��ʽ���userStore(token/userInfo/permissions/roles)+���localStorage(erp_user/erp_refresh_token)+����isRefreshing+ElMessage.warning��ʾ+router.replace��ת/login(���ظ���ת)+catch�鴫��ԭʼerror+pnpm buildͨ�� | |
| P0-002-003-002-004-001 | ����·�������� | 2026-05-31 | ? | ����cancelRequest.ts:CancelConfig�ӿ�(generateKey/skipCancel/cancelMessage)+defaultConfigʵ��+pendingMap(addPending�ظ�����ȡ��+removePending+ȡ��+ȡ��ָ��ҳ��)+CANCEL_WHITELIST_PATTERNS������(/auth/login/refresh-token)+isWhitelisted(responseType blob�Զ�������)+pnpm buildͨ�� | |
| P0-002-003-002-004-002 | ʵ��·��ע�������� | 2026-05-31 | ? | request.ts����cancelRequest(addPending/removePending/isWhitelisted)+guards.ts beforeEach����cancelPendingRequests·���л�ȡ��+fix cancelRequest.ts removePending��abort+cancelError�������CanceledError�ж�+pnpm buildͨ�� | 1570e76d |
| P0-002-003-003-001-001 | ����ӿ�·���뷽��ǩ�� | 2026-05-31T10:52 | ? | ����org/product/sale����ҵ��ģ��API���Ͷ���(6�ļ�)+��ӦAPIģ��CRUD����(RESTful URL+named export+TypeScript����),tsc --noEmit����ͨ�� | cbd471cd |
| P0-002-003-003-001-002 | ʵ�ֽӿ��߼� | 2026-05-31T11:00 | ? | Ϊorg/product/sale����ģ������batchDelete/import(FormData+60s��ʱ)/export(responseType:blob)���������߼�+ImportResultVO����,tsc --noEmitͨ�� | (pending) |
| P0-002-003-003-002-001 | ���巺����ṹ | 2026-05-31T11:15 | ? | ApiResponse<T=unknown>/PageResult<T=unknown>/PageQuery�������ͽӿڶ���,����Ĭ��ֵ��any��Ϊunknown��ǿ���Ͱ�ȫ | 83693563 |
| P0-002-003-003-002-002 | ʵ�־�̬�������� | 2026-05-31T11:55 | ? | api.d.ts����isSuccess/getErrorMessage/assertSuccess����ǩ��+apiHelper.ts����ʱʵ��(code===0�ϸ�Ƚ�+asserts��������+named export),tsc --noEmitͨ�� | 7c6bef74 |
| P0-002-003-003-002-003 | ��д��Ԫ���� | 2026-05-31T12:00 | ? | ��װvitest@4.1.7,����vitest.config.ts+src/types/__tests__/api.test-d.ts(13����26����),����ApiResponse/PageResult/PageQuery+isSuccess/getErrorMessage/assertSuccess���ͼ��,pnpm test:type 26passed+0 type errors+pnpm buildͨ�� | (pending) |
| P0-002-004-001-001-001 | ʵ�ֻ������ڷ��� | 2026-05-31T12:20 | ? | erp-ai-web/src/utils/date.ts:formatDate/parseDate/dateRange/relativeTime,dayjs+relativeTime���+zh-cn locale,�������޸�����,5�����ձ�׼ȫ��ͨ��,pnpm buildͨ�� | (pending) |
| P0-002-004-001-001-002 | ʵ�ֿ��ѡ������ | 2026-05-31T12:45 | ? | erp-ai-web/src/utils/date.ts����getDateShortcuts/getMonthRange/getQuarterRange+DateShortcut�ӿ�,7�����ѡ��(����/����/����/������/����/���7��/���30��),������һΪ��ʼ��+���ձ߽紦��,value����ʵʱ����,���������,4�����ձ�׼ͨ��,pnpm buildͨ�� | (pending) |
| P0-002-004-001-002-001 | ʵ�����ָ�ʽ������ | 2026-05-31T13:00 | ? | erp-ai-web/src/utils/number.ts:formatMoney/formatPercent/formatQty/formatFileSize+FormatNumberOptions�ӿ�,Intl.NumberFormat('zh-CN')ǧ��λ,�������޸�����,pnpm buildͨ��,5�����ձ�׼ȫ��ͨ�� | (pending) |
| P0-002-004-001-002-002 | ʵ�־��ȼ��㷽�� | 2026-05-31T13:20 | ? | erp-ai-web/src/utils/number.ts׷��add/subtract/multiply/divide/round+RoundingMode����+toSafeDecimal�ڲ�����,decimal.js���ȼ���,divide���㱣��,round���м�����(ROUND_HALF_EVEN)֧��4��ģʽ,5�����ձ�׼ȫ��ͨ��,pnpm test:typeͨ�� | (pending) |
| P0-002-004-001-003-001 | ʵ�ֶ��󷽷� | 2026-05-31T14:00 | ? | erp-ai-web/src/utils/object.ts:deepClone/deepMerge/pick/omit,WeakMap��ѭ������,Date/RegExp/Map/Set����֧��,�������޸�����,pnpm buildͨ��,4�����ձ�׼ȫ��ͨ�� | (pending) |
| P0-002-004-001-003-002 | ʵ�����鷽�� | 2026-05-31T14:30 | ? | erp-ai-web/src/utils/array.ts:arrayToTree/treeToArray/uniqueArray/flatten/groupBy,Map O(n)������+ջ����չƽ+Set/JSON����ȥ��+Record����,ȫ���Ͱ�ȫ������,pnpm buildͨ��,4�����ձ�׼ͨ�� | e5af4143 |
| P0-002-004-001-004-001 | ʵ��debounce/throttle | 2026-05-31T15:00 | ? | erp-ai-web/src/utils/debounce.ts:debounce/throttle,leading/trailing����+cancel/flush����+fn.apply(this,args)͸��+TypeErrorУ��,������������,pnpm buildͨ��,4�����ձ�׼ȫ��ͨ�� | 27eb9fe6 |
| P0-002-004-001-004-002 | ʵ�ָ߼����� | 2026-05-31T15:30 | ? | erp-ai-web/src/utils/debounce.ts׷��once(�״�ִ�л�����,�������ÿ�����)/beforeAfter(before��fn��after����)/withCount(Vue ref������)/useDebounce(composable��Ӧʽ����+onUnmounted����)/useThrottle(composable��Ӧʽ����+onUnmounted����),pnpm buildͨ��,4�����ձ�׼ȫ��ͨ�� | (pending) |
| P0-002-004-002-001-001 | ����ָ��Ӻ��� | 2026-05-31T16:00 | ? | erp-ai-web/src/directives/permission.ts:����PermissionValue����(string|string[]),����checkPermission����+permissionDirectiveָ��(Directive<HTMLElement,PermissionValue>),ʵ��mounted/updated/unmounted����,֧�ֵ�Ȩ�����������һƥ��,pnpm tsc --noEmit 0����+vite buildͨ�� | 4f8456fb |
| P0-002-004-002-001-002 | ʵ��ָ���߼� | 2026-05-31T16:30 | ? | erp-ai-web/src/directives/permission.ts:�л�useUserStoreȨ��Դ+admin��ɫ�������+removeElement��������+��ֵ����+Array.some��һƥ��+el.parentNode?.removeChild DOM�Ƴ�,tsc --noEmit 0���� | (pending) |
| P0-002-004-002-002-001 | ����ָ��Ӻ��� | 2026-05-31T17:00 | ? | erp-ai-web/src/directives/debounce.ts:ʵ��v-debounceָ��mounted/updated/unmounted����+parseDelay����arg�ӳ�+createDebounceHandler��������+ע�ᵽsetupDirectives,tsc --noEmit 0���� | 8fa6d7f3 |
| P0-002-004-002-002-002 | ʵ��ָ���߼� | 2026-05-31T17:30 | ? | erp-ai-web/src/directives/debounce.ts:��ȡcleanupDebounce��������+_debounceDelay�������binding.oldArg+createDebounceHandler����setTimeout/clearTimeout+parseDelay parseInt NaN����,tsc --noEmit 0���� | e5a3eea7 |
| P0-002-004-002-003-001 | ����ָ��Ӻ��� | 2026-05-31T17:45 | ? | erp-ai-web/src/directives/copy.ts:����CopyValue/CopyEl����+copyDirectiveָ��mounted/updated/unmounted����+clipboard API����+execCommand����+ע�ᵽsetupDirectives,tsc --noEmit 0���� | daaf308c |
| P0-002-004-002-003-002 | ʵ��ָ���߼� | 2026-05-31T18:10 | ? | erp-ai-web/src/directives/copy.ts:��ȡcopyToClipboard��������(clipboard API+execCommand����+left:-9999px����˸)+createCopyHandler��������(��ֵElMessage.warning+�ɹ�success+ʧ��error)+ָ��ӵ���,tsc --noEmit 0���� | 5b4d5812 |
| P0-002-004-003-001-001 | �����װ����ǩ�� | 2026-05-31T17:10 | ? | erp-ai-web/src/utils/message.ts:����MessageContent/MessageType/MessageOptions����+defaultOptionsĬ������+iconMapͼ��ӳ��+showSuccess/showError/showWarning/showInfo/confirm����ǩ��,tsc --noEmit 0���� | cd72d608 |
| P0-002-004-003-001-002 | ʵ�ַ�װ�߼� | 2026-05-31T17:15 | ? | erp-ai-web/src/utils/message.ts:ʵ����Ϣȥ��/ͳһ����/ͳһͼ��/confirm Promise��/VNode֧��/�߽紦��,vue-tsc --noEmit 0���� | a5fe20ce |
| P0-002-005-001-001-001 | �������԰��ṹ | 2026-05-31T17:42 | ? | erp-ai-web/src/i18n/index.ts:createI18nʵ��(legacy:false)+��Ӣ��messages(zh-CN/en-US)+fallbackLocale����+missing�ص�console.warn+setLanguageͬ��ElementPlus/dayjs/HTML lang+locale�洢,locale����ļ�(vue-i18n 9.14.5),vue-tsc --noEmit 0���� | 76d9d752 |
| P0-002-005-001-001-002 | ��д���Ĵ��� | 2026-05-31T18:30 | ? | erp-ai-web/src/main.ts:����ע��i18n(Pinia-i18n˳��)+provideEpLocale Element Plus locale������;env.d.ts:vue-i18n DefineLocaleMessage����+element-plus .mjsģ������;i18n/locales/zh-CN.ts+en-US.ts:��ʵcommon/status/validation����70+����;�޸�i18n/index.ts��localeContextKey+buildLocaleContext�������locale����,vue-tsc -b 0���� | 9fc34c44 |
| P0-002-004-003-002-001 | �����װ����ǩ�� | 2026-05-31T18:45 | ? | erp-ai-web/src/utils/notification.ts:����NotificationContent/NotificationType/NotificationPosition/NotifyOptions����+defaultOptionsĬ������+iconMapͼ��ӳ��+notify/notifySuccess/notifyError/notifyWarning/notifyInfo/clearAllNotifications����ǩ��,vue-tsc -b 0���� | 64ac7e60 |
| P0-002-004-003-002-002 | ʵ�ַ�װ�߼� | 2026-05-31T19:00 | ? | erp-ai-web/src/utils/notification.ts:ʵ��notifyImplȥ���߼�(activeNotifications Map)+ͳһ����(duration=4500ms/position='top-right'/showClose=true)+ͳһͼ��iconMap+clearAllNotifications����Map��ʵ��close+�߽紦��(title��warn/duration=0���Զ��ر�/��name����ȥ��),vue-tsc -b 0���� | 71770a62 |
| P0-002-005-001-002-001 | ����State�������ʼֵ | 2026-05-31T19:15 | ? | erp-ai-web/src/stores/modules/locale.ts:LocaleState�ӿ�(language/loadedLocales/availableLanguages)+getInitialLocale���ȼ���(app-language > navigator.language > zh-CN)+Pinia Setup Store���(defineStore+setup function)+loadedLocales��ʼ['zh-CN']+availableLanguages��Ӣ��ѡ��,vue-tsc -b 0���� | 00b05893 |
| P0-002-005-001-002-002 | ʵ��Actions | 2026-05-31T19:30 | ? | erp-ai-web/src/stores/modules/locale.ts:setLanguage(i18n/ElementPlus/dayjs/HTML lang�Ĵ�ͬ��+localStorage�־û�)+loadLocaleMessages(import.meta.glob��̬�������԰�)+epLocale������store,����5������ͨ�� | 4322c4ab |
| P0-002-005-001-002-003 | ʵ��Getters | 2026-05-31T19:45 | ? | erp-ai-web/src/stores/modules/locale.ts:currentLanguage/currentLanguageLabel/availableLanguages computed getter+isLocaleLoaded����ʽgetter,availableLanguages��ref��Ϊcomputed���ִ������޸����� | 3225c668 |
| P0-002-005-002-001-001 | ʵ�ֵ������� | 2026-05-31T17:15 | ? | erp-ai-web/src/i18n/locales/zh-CN/common.ts:52��ͨ�ô���(����/״̬/��ǩ/��ͷ/ռλ������)+export default����+CommonLocale���͵���,�ع�zh-CN.ts��common.ts���������ظ�,vue-tsc --noEmit 0���� | 08b97536 |
| P0-002-005-002-001-002 | ʵ�ֵ��빦�� | 2026-05-31T20:00 | ? | zh-CN/index.ts+en-US/index.ts:�ۺ�common/status/validation��ģ�����԰�����+��չ�����չ��+zh-CN/status.ts+validation.ts��ȡ����ģ���ļ�+en-US/common.ts+status.ts+validation.ts��ȡӢ��ģ��+ɾ����zh-CN.ts/en-US.ts���ļ�+��Ӣ�Ľṹһ��,vue-tsc --noEmit 0���� | dc5cc10d |
| P0-002-005-002-002-001 | ��д״̬�ı����� | 2026-05-31T20:30 | ? | erp-ai-web/src/i18n/locales/zh-CN/status.ts:Ƕ�׶���ṹstatus.{domain}.{code},�ĸ�ҵ����(audit/enable/order/payment),audit.pending/approved/rejected+enable.enabled/disabled+order.draft/submitted/confirmed/completed/cancelled+payment.unpaid/paid/refunded,export default+StatusLocale���͵���,vue-tsc --noEmit 0���� | cbbcc48f |
| P0-002-005-002-002-002 | ʵ��״̬�ı���Ⱦ���� | 2026-05-31T17:05 | ? | erp-ai-web/src/utils/status.ts:renderStatusText(statusType,statusCode)��װi18n.global.t����,StatusType��������(audit/enable/order/payment),Key��ʽstatus.{domain}.{code},δƥ�䷵��[statusType.statusCode]Ĭ���ı�+DEV����console.warn,��������+StatusType���͵���,vue-tsc --noEmit 0���� | a607a329 |
| P0-002-005-002-002-003 | ��֤״̬չʾ | 2026-05-31T17:30 | ? | �޸�en-US/status.ts��ƽ�ṹ��Ƕ�׽ṹ(��zh-CNһ��),13��״̬����Ӣ��ȫ������֤ͨ��,TypeScript����ͨ��,���Ա����Ѽ�¼ | 7d4aa0f0 |
| P0-002-005-002-003-001 | ��дУ����ʾ���� | 2026-05-31T17:05 | ? | ��дvalidation.ts:Ƕ�׽ṹrequired/format(phone/email/idCard/url)/length(min/max/range)/range(min/max/between)/custom(duplicate/invalid),ռλ��${label}/${min}/${max},export default+ValidationLocale���͵��� | (pending) |
| P0-002-005-002-003-002 | ʵ��У����ʾ��Ⱦ���� | 2026-05-31T17:30 | ? | erp-ai-web/src/utils/validation.ts:ValidationRule�ӿ�+replacePlaceholders(/\$\{(\w+)\}/g)+renderValidationMessage(rule,label)+getValidationMessages(rules,label)�ۺ�,��������,vue-tsc����� | fbc0db22 |
| P0-002-005-002-003-003 | ��֤У����ʾ | 2026-05-31T17:30 | ? | �޸�en-US/validation.ts��ƽ�ṹ��Ƕ�׽ṹ(��zh-CNһ��),{field}ռλ����${label}��ʽ,����·�����10����֤ȫ��ͨ��,vue-tsc --noEmit 0����,���Ա����Ѽ�¼ | b7b441c8 |
| P0-002-006-001-001-001 | ��дroot CSS���� | 2026-05-31T17:45 | ? | ����erp-ai-web/src/styles/variables.css(11��:root��ɫ����+6��html.dark��ɫģʽ����),main.ts����variables.css,vue-tsc����ͨ�� | �� |
| P0-002-006-001-001-002 | ����Tailwind�Զ�����ɫ | 2026-05-31T18:00 | ? | ��װtailwindcss+@tailwindcss/vite,����app.css(@theme��11����ɫTokenӳ��CSS����),main.ts����app.css,vite.config.ts����tailwindcss���,vue-tsc����ͨ�� | �� |
| P0-002-006-001-001-003 | ��֤��ɫϵͳ | 2026-05-31T18:15 | ? | ��֤:root 11����ɫ����+html.dark 6����ɫ����+app.css @theme Tailwindӳ��,�޸�element-plus.scssӲ�����var(--color-*)����,4/4����ͨ�� | 0720b90b |
| P0-002-006-001-002-001 | ��д���ߴ�CSS���� | 2026-05-31T18:00 | ? | erp-ai-web/src/styles/variables.css:׷��5��������(--spacing-xs/sm/md/lg/xl)+3��Բ�Ǳ���(--radius-sm/md/lg)+3����Ӱ����(--shadow-sm/md/lg)+html.dark��ɫ��Ӱ����,main.ts������variables.css | a678cd21 |
| P0-002-006-001-002-003 | ��֤���ϵͳ | 2026-05-31T17:10 | ? | ��֤:root��11�����/Բ��/��Ӱ����ȫ����ȷ����,app.css @theme Tailwind token������ȷ,html.dark��ɫ��Ӱ������ȷ,�����֤���� | 83adccf7 |
| P0-002-006-002-001-001 | �������/��չ���� | 2026-05-31T17:30 | ? | ��֤erp-ai-web/app.css @theme��:10����ɫToken(����CSS����)+5�����Token+3��Բ��Token+3����ӰToken,����variables.css CSS����ͬ��,main.ts������app.css,vue-tsc����� | (pending) |
| P0-002-006-002-001-002 | ��֤��ʽЧ�� | 2026-05-31T17:51 | ? | ��֤Tailwind@theme��ɫ/���/Բ��/��Ӱ7��,������С��ͼռλ�ļ�ʹ����ͨ��,npx vite build 1.09sͨ��,��֤����������,ȫ7/7������ͨ�� | (pending) |
| P0-002-006-003-001-001 | �������/��չ���� | 2026-05-31T17:55 | ? | ����element-override.scss:SCSS @forward����Element Plus��ɫ/Բ�Ǳ���+html.dark��ɫģʽEP CSS��������,main.ts����˳�����,pnpm buildͨ�� | 7af1a5f0 |
| P0-002-006-003-001-002 | ��֤��ʽЧ�� | 2026-05-31T18:05 | ? | ��֤EP��ɫ/Բ��/��Ӱ/��ɫģʽ/��Ӧʽ5��,pnpm buildͨ��,�����֤����,4/5ͨ��1���ͨ�� | 0d6f1a30 |
| P0-002-006-004-001-001 | ��дSCSS������ | 2026-05-31T18:15 | ? | ����_layout.scss:flex/grid/scroll/spacing/radius/shadow������,@each�������ɼ��,import��main.ts,pnpm buildͨ�� | c1ea8e84 |
| P0-002-006-004-001-002 | ��֤���ֹ����� | 2026-05-31T18:25 | ? | ��֤84��erp-������ȫ��ͨ������,flex/grid/scroll/spacing/radius/shadow��ȷ,��Tailwind�����޳�ͻ,1��NOTE(��Ӧʽ������@media) | 6550fb72 |
| P0-002-006-004-002-001 | ��дSCSS������ | 2026-05-31T18:35 | ? | ����_text.scss:ʡ��/��ɫ/����/����/����6�๤����,@eachѭ������,import��main.ts,pnpm buildͨ�� | 157cb739 |
| P0-002-006-004-002-002 | ��֤�ı�װ�ι����� | 2026-05-31T18:45 | ? | ��֤�ı�ʡ��/��ɫ/����/����/����/����/��Ӱ������,�޸�erp-transition-allʹ��transition:all��Ϊָ������,pnpm buildͨ�� | ce69c4e3 |
| P0-002-006-005-001-001 | ��дv-virtual-scrollָ�� | 2026-05-31T19:00 | ? | ����virtual-scroll.ts:ʵ�ֿ���������Ⱦ��buffer��/transform translateY��λ/��̬�߶�heightCache/ResizeObserver/passive scroll/ע�ᵽdirectives/index.ts,vue-tsc+vite buildͨ�� | 989d3aec |
| P0-002-006-005-001-002 | ��֤������� | 2026-05-31T19:15 | ? | 7/7��֤��ȫ��ͨ��:����������Ⱦ/translateY��λ/heightCache����/passive scroll/unmounted����/����������any/����dev����ҳ(100k����+����ȫ�����԰�ť),pnpm buildͨ�� | 695b7533 |
| P0-002-006-005-002-002 | ��֤ͼƬ������ | 2026-05-31T19:50 | ? | 6����֤ȫ��ͨ��:�����ⲻ����/200pxԤ����/unobserve����/onerror����SVGռλͼ/disconnect�ͷ�/URL�����ؼ���+�޸�DEFAULT_ERROR_IMG��Ϊinline SVG data URI,pnpm buildͨ�� | 15abd820 |
| P0-002-006-005-003-001 | ����vite.config.ts�����Ż� | 2026-05-31T21:00 | ? | minify:terser+terserOptions����(drop_console/drop_debugger�����Ƴ�)+sourcemap������������+defineConfig��ͷ������ʽ+��װterser 5.48.0+manualChunks������ʽ�ְ�vue/elementPlus/vendor(Vite 8/Rolldown����),pnpm buildͨ��(5.04s) | 8827ef9f |
| P0-002-006-005-003-002 | ��֤�����Ż� | 2026-05-31T19:00 | ? | ��֤7�������+�޸�element-plus�ְ�˳��(@element-plus/icons-vue��vue�����󲶻�)+�޸�console����(drop_console��pure_funcs����warn/error)+vue chunk 332KB gzipped(�Գ�300KB��@vueuse��Rolldown�����޷�����) | 8c06e7e9 |
| P0-002-006-006-001-001 | ��д�ⲿ��Դ���������� | 2026-05-31T21:15 | ? | ����cdn-fallback.ts(����/����CDN�Զ��л�+��ʱ����+preloadԤ����)+.env.development/.env.production����VITE_EXTERNAL_CDN_BASE/VITE_CDN_FALLBACK_URL+index.html����preconnect | 8d86e81d |
| P0-002-006-006-001-002 | ��֤CDN�л� | 2026-05-31T18:32 | ? | ��֤7��CDN�л�+����+����ȫ��ͨ��:cdn-fallback.ts����CDN fonts.loli.net��ȷ/��ʱ3000ms+�Զ��л�����Դ/��������VITE_EXTERNAL_CDN_BASE��ȷ/index.html preconnect��Ч/TypeScript���������/Vite�����ɹ�(4.72s) | 56b53ae1 |
| P0-003-007-012-001-003 | ��֤��дinv_disassembly_detail��ж���ӱ�DDL | 2026-06-03T18:30 | ? | V20260603005��֤SQL(����17+�ӱ�17+Flyway��֤��35���ѯ)+��̬��������(34����/76�ֶ�COMMENT/22��չ�ֶ�/11�����ֶ�/DECIMAL(18,8)����ȫ��ͨ��) | daf7e4bb |

### ģ�����: P0-002 ?

### P0-003 - ���ݿ�����ܹ��

| ������ | �������� | ���ʱ�� | ״̬ | ժҪ | SHA |
|---------|---------|---------|:---:|------|-----|
| P0-003-001-001-001-001 | ��дCREATE DATABASE��� | 2026-05-31T20:11 | ? | db/migration/V20260526001__schema_related.sql:PostgreSQL����erp_db���ݿ�(UTF8����/zh_CN.UTF-8����/200����)+erp_base/erp_tenant˫Schema���⻧����+�ݵ������(DO$$IF NOT EXISTS/IF NOT EXISTS)+ALTER DATABASE����·������+COMMENTע������,docs/specs/P0_003_001_001_001_001_spec.md:���ݿ�����/Schema���/���⻧����/ִ��˵��/��֤����/�������߼�¼ | 482f1299 |
| P0-003-001-001-001-002 | ִ��DDL����֤ | 2026-05-31T18:32 | ? | db/migration/V20260526001__verify_chema.sql:DDL��֤��ѯ�ű�(���ݿ�/SCHEMA/����/����·��/Flyway��ʷ7����֤��)+docs/verification/chema_verify_report.md:������֤����(���ձ�׼���/�״���ʾȷ��/ִ��˵��) | eaf51737 |
| P0-003-001-002-001-001 | ��д�����ֶ�DDL | 2026-05-31T18:50 | ? | db/migration/V20260526001__schema_related.sql:����Step5�����ֶλ�������(10�ֶ��嵥+DDLģ��+���Ҫ��+����ģ��)+docs/specs/P0_003_001_002_001_001_spec.md:�����ֶι淶�ĵ�(�ֶζ���/���Ҫ��/���⻧/��ɾ��/�ֹ���) | fe133a62 |
| P0-003-001-002-001-002 | ��дĬ��ֵ��Լ�� | 2026-05-31T18:35 | ? | db/migration/V20260526001__schema_related.sql:����Step6Ĭ��ֵ�淶+Step7Լ���淶+Step8ʾ������+Step9 DML�ݵ���ʾ��+docs/specs/P0_003_001_002_001_002_spec.md:Ĭ��ֵ��Լ�������淶�ĵ� | b319c32a |
| P0-003-001-002-001-003 | ��֤�淶 | 2026-05-31T18:35 | ? | db/migration/V20260526001__verify_chema.sql:���������ֶι淶��֤(��8-12)��10�ֶ�������/NOT NULLԼ��/����Ψһ����/���⻧����/COMMENTע��/Flyway��֤+docs/verification/chema_verify_report.md:������֤����(13��ȫPASS) | ce548818 |
| P0-003-002-001-001-001 | ��д�����ֶ�DDL | 2026-05-31T18:55 | ? | db/migration/V20260531001__task_P0_003_002_001_001_001.sql:����erp_base.public_field_spec�淶��¼��(10ͨ���ֶ�Ԫ����DDL/Ĭ��ֵ/Լ��/����ģ��/COMMENTģ��/��չ�ֶ�ģ��/��������ʾ��)+docs/specs/P0_003_002_001_001_001_spec.md:�����ֶι淶��֤�ĵ�(10�ֶζ���/Լ������/��֤���) | fee4b649 |
| P0-003-002-001-001-002 | ��дĬ��ֵ��Լ�� | 2026-05-31T18:42 | ? | db/migration/V20260531002__task_P0_003_002_001_001_002.sql:����erp_base.public_default_value_specĬ��ֵ�淶��+erp_base.public_constraint_specԼ���淶��+fn_validate_common_fields/fn_batch_validate_schema����PL/pgSQLУ�麯��+��������DDLģ��+docs/specs/P0_003_002_001_001_002_spec.md:Ĭ��ֵ��Լ���淶��֤�ĵ� | 15cb2b8a |
| P0-003-002-001-001-003 | ��֤�淶 | 2026-05-31T18:50 | ? | db/migration/V20260531003__task_P0_003_002_001_001_003.sql:��֤SQL�ű�(7����֤�����ṹ/����������/Լ���Ϲ���/COMMENT������/������֤/Flyway��ʷ/�ۺϻ���)+docs/specs/P0_003_002_001_001_003_spec.md:��֤�淶�ĵ�(32����֤�嵥/10����ļ��) | 2c67a3d3 |
| P0-003-002-002-001-001 | ��дCREATE TABLE��� | 2026-05-31T18:50 | ? | db/migration/V20260531004__task_P0_003_002_002_001_001.sql:10��ϵͳ���ı�DDL(sys_user/sys_role/sys_menu/sys_user_role/sys_user_dept/sys_role_menu/sys_role_data_scope/sys_role_field_permission/sys_user_group/sys_user_group_member),decimal(18,8)ͳһ����,COMMENTע������,�����Լ�� | b1324ef0 |
| P0-003-002-002-001-002 | ��дϵͳ���ı�������Լ�� | 2026-05-31T18:50 | ? | db/migration/V20260531005__task_P0_003_002_002_001_002.sql:10�ű�������Լ��(12UK��WHERE is_deleted=false+22IDX),ȫ��tenant_id������������,�����淶uk_/idx_+docs/specs/P0_003_002_002_001_002_spec.md | 28270e3d |
| P0-003-002-002-001-003 | ��֤��дϵͳ���ı�DDL | 2026-05-31T19:00 | ? | db/migration/V20260526001__task_P0_003_002_002_001_003.sql:8������֤SQL(��������/�ֶ�������/Լ��/����/COMMENT/Flyway/���/�ۺ�ժҪ)+docs/specs/P0_003_002_002_001_003_spec.md:��̬���ȫ��ͨ��,10��DDL�淶һ��,31�����Ϲ�,COMMENT������100% | 3212f364 |
| P0-003-002-003-001-001 | ��дCREATE TABLE��� | 2026-05-31T19:10 | ? | db/migration/V20260531006__task_P0_003_002_003_001_001.sql:2����֤��ر�DDL(sys_login_log/sys_oper_log),decimal(18,8)ͳһ����,COMMENTע������,�����Լ��+docs/specs/P0_003_002_003_001_001_spec.md | 312f831b |
| P0-003-002-003-001-002 | ��д��֤��ر�������Լ�� | 2026-05-31T19:25 | ? | db/migration/V20260531007__task_P0_003_002_003_001_002.sql:2�ű�12������(sys_login_log 6��+sys_oper_log 6��),ȫ��tenant_id������������,����idx_ǰ׺,Flyway V20260531007�޳�ͻ+docs/specs/P0_003_002_003_001_002_spec.md | 245382e2 |
| P0-003-002-003-001-003 | ��֤��д��֤��ر�DDL | 2026-05-31T19:40 | ? | db/migration/V20260526001__task_P0_003_002_003_001_003.sql:9������֤SQL(��������/�ֶ�������/����/COMMENT/Flyway/���/����)��195��+docs/specs/P0_003_002_003_001_003_spec.md:��̬���ȫ��ͨ��,2��DDL�淶һ��,12����ȫ��tenant_id����,COMMENT������100% | fdbddb0c |
| P0-003-002-004-001-001 | ��дCREATE TABLE��䣨ϵͳ�������� | 2026-05-31T19:50 | ? | db/migration/V20260531008__task_P0_003_002_004_001_001.sql:10��ϵͳ������DDL(sys_param/sys_dict_type/sys_dict_data/sys_code_rule/sys_code_rule_segment/sys_operation_log/sys_data_view/sys_data_view_field/sys_notice/sys_doc_config),decimal(18,8)ͳһ����,COMMENTע������,�����Լ��+docs/specs/P0_003_002_004_001_001_spec.md | 17f573b7 |
| P0-003-002-004-001-002 | ��дϵͳ������������Լ�� | 2026-05-31T19:20 | ? | db/migration/V20260531009__task_P0_003_002_004_001_002.sql:10��ϵͳ������18������(7������Ψһ������WHERE is_deleted=false+11��B-Tree��ѯ����),ȫ��tenant_id������������,COMMENT ON INDEX����+docs/specs/P0_003_002_004_001_002_spec.md | 4a00776b |
| P0-003-002-005-001-001 | ��дCREATE TABLE��� | 2026-06-01T00:00 | ? | db/migration/V20260531011__task_P0_003_002_005_001_001.sql:1���ƶ��˲˵���DDL(sys_mobile_menu),10��ͨ���ֶ�+9��ҵ���ֶ�+��չ�ֶ�,decimal(18,8)ͳһ����,COMMENTע������,�����Լ��+docs/specs/P0_003_002_005_001_001_spec.md | 9179a4a1 |
| P0-003-002-005-001-002 | ��д�ƶ�����ر�������Լ�� | 2026-06-01T08:00 | ? | db/migration/V20260601001__task_P0_003_002_005_001_002.sql:5������(4��ͨ+1����Ψһ),tenant_id������������,WHERE is_deleted=false,COMMENT ON INDEX����+docs/specs/P0_003_002_005_001_002_spec.md | 15090306 |
| P0-003-002-005-001-003 | ��֤��д�ƶ�����ر�DDL | 2026-06-01T09:00 | ? | db/migration/V20260526001__task_P0_003_002_005_001_003.sql:8������֤SQL(��������/ͨ���ֶ�������/�ֶ�����Լ��/����/COMMENTע��/Flyway/������/�ۺ�ժҪ)+docs/specs/P0_003_002_005_001_003_spec.md:��̬���ȫ��ͨ��,sys_mobile_menu DDL�淶һ��,5����ȫ��tenant_id����,COMMENT������100% | 224bd591 |
| P0-003-002-006-001-001 | ��д��������DDLԼ����ö��ע�� | 2026-06-01T10:00 | ? | db/migration/V20260601002__task_P0_003_002_006_001_001.sql:����erp_base.bill_main_field_spec�淶��+fn_validate_bill_fieldsУ�麯��+DDLģ��/����Ψһ����ģ��/bill_statusö��ע��(0=�ݸ�/1=�����/2=�����/3=�ѹر�/4=������)+docs/specs/P0_003_002_006_001_001_spec.md | 60b06180 |
| P0-003-002-007-001-001 | ��дDDL | 2026-06-01T12:00 | ? | db/migration/V20260526001__task_P0_003_002_007_001_001.sql:����erp_base.detail_product_field_spec�淶��(16�ֶ�:3�ṹ�ֶ�+13�����ֶ�)+fn_validate_detail_product_fieldsУ�麯��+DDLƬ��ģ��/����ģ��/�����ɹ�����ϸ����ʾ��/����ԭ��������Ҫ��+docs/specs/P0_003_002_007_001_001_spec.md | 716ce62e |
| P0-003-002-007-001-002 | ��д����Լ��˵�� | 2026-06-01T13:00 | ? | db/migration/V20260526001__task_P0_003_002_007_001_002.sql:����erp_base.detail_snapshot_constraint����Լ��Ԫ���ݱ�(50������:10�����ֶΡ�5����״̬)+fn_validate_snapshot_constraintsУ�麯��+�������Ĺ����ĵ�/״̬ת������/��������嵥/ǰ�˽����淶+docs/specs/P0_003_002_007_001_002_spec.md | 3a890daf |
| P0-003-002-008-001-001 | ��дDDL�淶 | 2026-06-01T13:30 | ? | db/migration/V20260526001__atis_related.sql:����erp_base.tenant_isolation_constraint���⻧����Լ��Ԫ���ݱ�(29����ע��ҵ���)+fn_validate_tenant_isolation_ddl����У�麯��+fn_validate_all_tenant_isolation_ddl����У�麯��+�������Ĺ����ĵ�/DDL��׼ģ��/�����������/��������嵥+docs/specs/P0_003_002_008_001_001_spec.md | bd695138 |
| P0-003-002-008-001-002 | ��дMyBatis-Plus TenantLineInnerInterceptor | 2026-06-01T13:45 | ? | TenantInterceptor.java:����@Componentʵ��TenantLineHandler+MybatisPlusConfig.java�ع��Ƴ������ڲ���+docs/specs/P0_003_002_008_001_002_spec.md | ba1f5d2e |
| P0-003-002-009-001-001 | ��дDDL | 2026-06-01T14:00 | ? | db/migration/V20260526001__task_P0_003_002_009_001_001.sql:����erp_base.doc_detail_location/doc_detail_batch/doc_detail_serial�������������ӱ�(����ҵ���ֶ�+22��չ+10����+����+COMMENT)+fn_validate_aux_tableУ�麯��+docs/specs/P0_003_002_009_001_001_spec.md | 9bac0e52 |
| P0-003-003-001-001-001 | ��дCREATE TABLE��� | 2026-06-01T14:30 | ? | db/migration/V20260601003__create_org_company.sql:org_company��˾��DDL(10ͨ���ֶ�+6ҵ���ֶ�+��չ�ֶ�+COMMENTע��+decimal(18,8)����)+�ع��ű� | 9158ccaa |
| P0-003-003-001-001-002 | ��дorg_company��˾��������Լ�� | 2026-06-01T15:00 | ? | db/migration/V20260601004__create_org_company_indexes.sql:����Լ��������+����Ψһ����(WHERE is_deleted=false)+���⻧��������(tenant_id����)+ҵ���ѯ����+�ع��ű� | 06849415 |
| P0-003-003-001-001-003 | ��֤��дorg_company��˾��DDL | 2026-06-01T15:30 | ? | db/migration/V20260526001__verify_org_company.sql:13����֤SQL(��������/�ֶ�������/ͨ���ֶ�/����/NOT NULL/����/����Ψһ����/���⻧����/COMMENT/���/Flyway)+docs/verification/org_company_verify_report.md:������֤����(��������:��������codeӦΪcompany_code/22����չ�ֶ�ȱCOMMENT) | 908a4114 |
| P0-003-003-002-001-001 | ��дCREATE TABLE��� | 2026-06-01T16:00 | ? | db/migration/V20260601005__create_org_department.sql:org_department���ű�DDL(10ͨ���ֶ�+6ҵ���ֶ�+��չ�ֶ�+COMMENTע��+decimal(18,8)����)+�ع��ű� | 9adc627f |
| P0-003-003-002-001-002 | ��дorg_department���ű�������Լ�� | 2026-06-01T16:30 | ? | db/migration/V20260601006__create_org_department_indexes.sql:����Լ��������+����Ψһ����(WHERE is_deleted=false)+���⻧��������(tenant_id����)+ҵ���ѯ����(����/���/״̬/����)+�ع��ű� | fe4f5191 |
| P0-003-003-002-001-003 | ��֤��дorg_department���ű�DDL | 2026-06-01T17:00 | ? | db/migration/V20260526001__verify_org_department.sql+��֤����:����CRITICAL������������(code��dept_code)��WARNING ext�ֶ�COMMENTȱʧ | 67748186 |
| P0-003-003-003-001-001 | ��дCREATE TABLE��� | 2026-06-01T17:15 | ? | db/migration/V20260601007__create_org_position.sql:org_position��λ��DDL(10ͨ���ֶ�+5ҵ���ֶ�+��չ�ֶ�+COMMENTע��+decimal(18,8)����)+�ع��ű� | 68706813 |
| P0-003-003-003-001-002 | ��дorg_position��λ��������Լ�� | 2026-06-01T17:30 | ? | db/migration/V20260601008__create_org_position_indexes.sql:����Լ��������+����Ψһ����(WHERE is_deleted=false)+���⻧��������(tenant_id����)+ҵ���ѯ����+�ع��ű� | 98d987ff |
| P0-003-003-003-001-003 | ��֤��дorg_position��λ��DDL | 2026-06-01T18:00 | ? | db/migration/V20260526001__verify_org_position.sql(14����֤SQL)+docs/verification/org_position_verify_report.md(5/5����ͨ��,������������) | a8636532 |
| P0-003-003-004-001-001 | ��дCREATE TABLE��� | 2026-06-01T18:30 | ? | db/migration/V20260601009__create_org_employee.sql:org_employeeԱ����DDL(10ͨ���ֶ�+7ҵ���ֶ�+��չ�ֶ�+COMMENTע��+decimal(18,8)����)+�ع��ű� | c28ba012 |
| P0-003-003-004-001-002 | ��дorg_employeeԱ����������Լ�� | 2026-06-01T19:00 | ? | db/migration/V20260601010__create_org_employee_indexes.sql:����Լ��������+����Ψһ����(WHERE is_deleted=false)+���⻧��������(tenant_id����)+ҵ���ѯ����+�ع��ű� | 9e7e0f03 |
| P0-003-003-004-001-003 | ��֤��дorg_employeeԱ����DDL | 2026-06-01T19:30 | ? | db/migration/V20260526001__verify_org_employee.sql(14����֤SQL)+docs/verification/org_employee_verify_report.md(5/5����ͨ��,1��WARNING:22����չ�ֶ�ȱCOMMENT) | dcdc09ad |
| P0-003-004-001-001-001 | ��дCREATE TABLE��� | 2026-06-01T19:50 | ? | db/migration/V20260601011__create_prod_product_class.sql:prod_product_class��Ʒ�����DDL(10ͨ���ֶ�+4ҵ���ֶ�+22��չ�ֶ�+COMMENTע��+decimal(18,8)����)+�ع��ű� | 435f0d04 |
| P0-003-004-001-001-002 | ��дprod_product_class��Ʒ�����������Լ�� | 2026-06-01T20:00 | ? | db/migration/V20260601012:����status��+PK������Ϊpk_prod_product_class+����Ψһ����uk(WHERE is_deleted=false)+���⻧��������(tenant_id����)+ҵ���ѯ����+�ع��ű� | ced1f706 |
| P0-003-004-001-001-003 | ��֤��дprod_product_class��Ʒ�����DDL | 2026-06-01T21:00 | ? | db/migration/V20260601013__verify_prod_product_class.sql(15����֤SQL)+docs/verification/prod_product_class_verify_report.md(1CRITICAL:code/class_code������ƥ��+1WARNING:22��չ�ֶ�ȱCOMMENT) | 0f9a945d |
| P0-003-004-002-001-001 | ��дprod_product��Ʒ����CREATE TABLE��� | 2026-06-01T22:00 | ? | db/migration/V20260601014:prod_product��DDL(10ͨ���ֶ�+8ҵ���ֶ�+22��չ�ֶ�,decimal(18,8),ȫCOMMENT)+�ع��ű� | a36b887f |
| P0-003-004-002-001-002 | ��дprod_product��Ʒ����������Լ�� | 2026-06-01T23:00 | ? | db/migration/V20260601015:PK������Ϊpk_prod_product+����Ψһ����uk(WHERE is_deleted=false)+���⻧��������(tenant_id����)+ҵ���ѯ����(class_id/base_unit_id/created_at/name)+�ع��ű� | 1d70675f |
| P0-003-004-002-001-003 | ��֤��дprod_product��Ʒ����DDL | 2026-06-01T23:30 | ? | db/migration/V20260526001__verify_prod_product.sql(16����֤SQL)+docs/verification/prod_product_verify_report.md(1CRITICAL:code/product_code������ƥ��+1WARNING:22��չ�ֶ�ȱCOMMENT) | �� |
| P0-003-004-003-001-001 | ��дCREATE TABLE���(prod_product_unit) | 2026-06-01T23:45 | ? | db/migration/V20260601016:prod_product_unit��DDL(10ͨ���ֶ�+4ҵ���ֶ�,decimal(18,8),ȫCOMMENT)+�ع��ű� | e7691af8 |
| P0-003-004-003-001-002 | ��дprod_product_unit��Ʒ�൥λ��������Լ�� | 2026-06-01T23:59 | ? | db/migration/V20260601017:PK������Ϊpk_prod_product_unit+����Ψһ����uk(WHERE is_deleted=false, product_id+unit_id)+���⻧��������(tenant_id����)+ҵ���ѯ����(is_base_unit)+�ع��ű� | 6ed96676 |
| P0-003-004-003-001-003 | ��֤��дprod_product_unit��Ʒ�൥λ��DDL | 2026-06-01T10:00 | ? | db/migration/V20260526001__verify_prod_product_unit.sql(16����֤SQL)+docs/verification/prod_product_unit_verify_report.md(ȫ��PASS, 0CRITICAL, COMMENT������100%) | 31d9f412 |
| P0-003-004-004-001-001 | ��дCREATE TABLE���(prod_product_control) | 2026-06-01T12:00 | ? | db/migration/V20260601018:prod_product_control��DDL(10ͨ���ֶ�+4ҵ���ֶ�,ȫCOMMENT)+�ع��ű� | 52f7d036 |
| P0-003-004-004-001-002 | ��дprod_product_control��Ʒ���Ʋ��Ա�������Լ�� | 2026-06-01T13:30 | ? | db/migration/V20260601019:PK������Ϊpk_prod_product_control+����Ψһ����uk(WHERE is_deleted=false, tenant_id+product_id)+���⻧��������(tenant_id����)+ҵ���ѯ����(product_id/is_inventory)+�ع��ű� | bd0c660a |
| P0-003-004-004-001-003 | ��֤��дprod_product_control��Ʒ���Ʋ��Ա�DDL | 2026-06-01T14:00 | ? | db/migration/V20260601020(15����֤SQL)+docs/verification/prod_product_control_verify_report.md(ȫ��PASS, 0CRITICAL, COMMENT������100%) | �� |
| P0-003-004-005-001-001 | ��дCREATE TABLE���(prod_product_safety_stock) | 2026-06-01T15:00 | ? | db/migration/V20260601021:prod_product_safety_stock��DDL(10ͨ���ֶ�+5ҵ���ֶ�,ȫCOMMENT)+�ع��ű� | 37b00bd7 |
| P0-003-004-005-001-002 | ��дprod_product_safety_stock��Ʒ��ȫ����������Լ�� | 2026-06-01T15:30 | ? | db/migration/V20260601022:PK������Ϊpk_prod_product_safety_stock+����Ψһ����uk(WHERE is_deleted=false)+���⻧��������(tenant_id����)+ҵ���ѯ����+�ع��ű� | 68fde514 |
| P0-003-004-005-001-003 | ��֤��дprod_product_safety_stock��Ʒ��ȫ����DDL | 2026-06-01T16:00 | ? | db/migration/V20260601023(15����֤SQL)+docs/verification/prod_product_safety_stock_verify_report.md(ȫ��PASS, 0CRITICAL, COMMENT������100%) | eaf9b7e9 |
| P0-003-004-006-001-001 | ��дCREATE TABLE���(prod_product_attachment) | 2026-06-01T16:30 | ? | db/migration/V20260601024:prod_product_attachment��DDL(10ͨ���ֶ�+9ҵ���ֶ�,ȫCOMMENT)+�ع��ű� | b7269885 |
| P0-003-004-006-001-002 | ��дprod_product_attachment��Ʒ������������Լ�� | 2026-06-01T16:35 | ? | db/migration/V20260601025:PK������+����Ψһ����uk(WHERE is_deleted=false)+���⻧��������(tenant_id����)+ҵ���ѯ����+�ع��ű� | 6d1251bd |
| P0-003-004-006-001-003 | ��֤��дprod_product_attachment��Ʒ������DDL | 2026-06-01T16:45 | ? | db/migration/V20260601026(17����֤SQL)+docs/verification/prod_product_attachment_verify_report.md(ȫ��PASS, 0CRITICAL, COMMENT������100%) | f548a1fb |
| P0-003-002-011-001-001 | ��д�淶 | 2026-06-01T17:20 | ? | docs/specs/P0_003_002_011_001_001_spec.md(7��base_qty���Ĺ淶+�����߼�+У�����)+db/migration/V20260526001__base_qty_related.sql(3��PL/pgSQL����:fn_calc_base_qty/fn_validate_base_qty/fn_reverse_calc_qty+�淶�ĵ�ע��) | (pending) |
| P0-003-002-012-001-001 | ��д�淶 | 2026-06-01T17:50 | ? | docs/specs/db_naming_convention.md+db_data_type_spec.md+db_index_spec.md(���ݿ�����/��������/�����������淶�ĵ�,����ΨһԼ���躬is_deleted+���⻧����+decimal(18,8)����) | fade7c18 |
| P0-003-002-013-001-001 | ��д���ȹ��� | 2026-06-01T18:00 | ? | docs/specs/P0_003_002_013_001_001_spec.md(7��:�������ȼ���ϵ+β�������+��������)+db/migration/V20260526001__task_P0_003_002_013_001_001.sql(4��PL/pgSQL����+12��Ԥ�þ��Ȳ���+�淶ע��) | (pending) |
| P0-003-004-007-001-001 | ��дCREATE TABLE���(prod_product_standard_price) | 2026-06-01T18:10 | ? | db/migration/V20260601027:prod_product_standard_price��DDL(10ͨ���ֶ�+12ҵ���ֶ�,decimal(18,8),ȫCOMMENT)+�ع��ű� | d3ed75df |
| P0-003-004-007-001-002 | ��дprod_product_standard_price��Ʒ��׼�۱�������Լ�� | 2026-06-01T18:20 | ? | db/migration/V20260601028:PK������Ϊpk_prod_product_standard_price+����Ψһ����uk(WHERE is_deleted=false, tenant_id+product_id)+���⻧��������(tenant_id����)+ҵ���ѯ����+�ع��ű� | 623a556e |
| P0-003-004-007-001-003 | ��֤��дprod_product_standard_price��Ʒ��׼�۱�DDL | 2026-06-01T18:35 | ? | db/migration/V20260601029:20����֤��ѯSQL+docs/verification/prod_product_standard_price_verify_report.md:12����֤����(22�ֶ�+10����+100%COMMENT+decimal(18,8)+�����) | a0a83b37 |
| P0-003-004-008-001-001 | ��дCREATE TABLE���(prod_product_purchase_price) | 2026-06-01T18:50 | ? | db/migration/V20260601030:prod_product_purchase_price��DDL(10ͨ���ֶ�+13ҵ���ֶ�,decimal(18,8),ȫCOMMENT)+�ع��ű� | 74f42302 |
| P0-003-004-008-001-002 | ��дprod_product_purchase_price��Ʒ���ۺ˶���������Լ�� | 2026-06-01T19:00 | ? | db/migration/V20260601031:PK������+����Ψһ����uk(WHERE is_deleted=false, tenant_id+product_id+supplier_id)+���⻧��������(tenant_id����)+ҵ���ѯ����+�ع��ű� | 5d5a49ad |
| P0-003-004-008-001-003 | ��֤��дprod_product_purchase_price��Ʒ���ۺ˶���DDL | 2026-06-01T19:10 | ? | db/migration/V20260601032:20����֤��ѯSQL+docs/verification/prod_product_purchase_price_verify_report.md:12����֤����(23�ֶ�+12����+100%COMMENT+decimal(18,8)+�����) | fbd88e4a |
| P0-003-004-009-001-001 | ��дCREATE TABLE���(prod_product_sale_price) | 2026-06-01T19:20 | ? | db/migration/V20260601033:prod_product_sale_price��DDL(10ͨ���ֶ�+13ҵ���ֶ�,decimal(18,8),ȫCOMMENT)+�ع��ű� | 8f784931 |
| P0-003-004-009-001-002 | ��дprod_product_sale_price��Ʒ���ۺ˶���������Լ�� | 2026-06-01T19:35 | ? | db/migration/V20260601034:PK������+����Ψһ����uk(WHERE is_deleted=false, tenant_id+product_id+customer_id)+���⻧��������(tenant_id����)+ҵ���ѯ����+�ع��ű� | dcafe8db |

| P0-003-004-009-001-003 | ��֤��дprod_product_sale_price��Ʒ���ۺ˶���DDL | 2026-06-01T19:50 | ? | db/migration/V20260601035:20����֤��ѯSQL+docs/verification/prod_product_sale_price_verify_report.md:12����֤����(23�ֶ�+12����+100%COMMENT+decimal(18,8)+�����) | a5ba3446 |
| P0-003-004-010-001-001 | ��дCREATE TABLE��� | 2026-06-01T20:00 | ? | db/migration/V20260601036:CREATE TABLE prod_product_competitor(17ҵ���ֶ�+10ͨ���ֶ�+COMMENT)+�ع��ű� | ede91a34 |
| P0-003-004-010-001-002 | ��дprod_product_competitor��Ʒ��Ʒ��������Լ�� | 2026-06-01T20:10 | ? | db/migration/V20260601037:9������(1PK+1UK+2���⻧+5ҵ���ѯ)+�ع��ű� | d68840ce |
| P0-003-004-010-001-003 | ��֤��дprod_product_competitor��Ʒ��Ʒ��DDL | 2026-06-01T20:25 | ? | db/migration/V20260601038:20����֤��ѯSQL+docs/verification/prod_product_competitor_verify_report.md:12����֤����(27�ֶ�+10����+100%COMMENT+decimal(18,8)+�����) | e73c17f5 |
| P0-003-004-011-001-001 | ��дCREATE TABLE��� | 2026-06-01T20:35 | ? | db/migration/V20260601039:CREATE TABLE prod_product_bom_detail(12ҵ���ֶ�+14�����ֶ�+10ͨ���ֶ�+COMMENT)+�ع��ű� | (���ύ) |
| P0-003-004-011-001-002 | ��дprod_product_bom_detail��ƷBOM���ӱ�������Լ�� | 2026-06-01T20:50 | ? | db/migration/V20260601040:9������(1PK rename+1UK+3���⻧+5ҵ���ѯ)+�ع��ű� | 23580f90 |
| P0-003-004-011-001-003 | ��֤��дprod_product_bom_detail��ƷBOM���ӱ�DDL | 2026-06-01T21:10 | ? | db/migration/V20260601041:20����֤��ѯSQL+docs/verification/prod_product_bom_detail_verify_report.md:13����֤����(30�ֶ�+10����+100%COMMENT+decimal(18,8)+12�����ֶ�+�����) | 567ee77b |
| P0-003-004-012-001-001 | ��дCREATE TABLE��� | 2026-06-01T21:30 | ? | db/migration/V20260601042:CREATE TABLE prod_product_process_price(17ҵ���ֶ�+10ͨ���ֶ�+COMMENT,decimal(18,8)ͳһ����,parent_id֧�����β㼶)+�ع��ű�+spec�ĵ� | 5afd1f5f |
| P0-003-004-012-001-002 | ��дprod_product_process_price��Ʒ�������ӱ�������Լ�� | 2026-06-01T21:50 | ? | db/migration/V20260601043:PK������+����Ψһ����uk_tenant_code(WHERE is_deleted=false)+3�����⻧��������+4��ҵ���ѯ����+�ع��ű� | 9936d271 |
| P0-003-004-012-001-003 | ��֤��дprod_product_process_price��Ʒ�������ӱ�DDL | 2026-06-01T22:10 | ? | db/migration/V20260601044:20����֤��ѯSQL+docs/verification/prod_product_process_price_verify_report.md(27�ֶ�/9����/100%COMMENT������,0CRITICAL 0WARNING) | e82de627 |
| P0-003-004-013-001-001 | ��дCREATE TABLE��� | 2026-06-01T22:30 | ? | db/migration/V20260601045:CREATE TABLE prod_standard_process(10ҵ���ֶ�+10ͨ���ֶ�+COMMENT,decimal(18,8)ͳһ����)+�ع��ű� | 4fc0990e |
| P0-003-004-013-001-002 | ��дprod_standard_process��׼�����������Լ�� | 2026-06-01T22:35 | ? | db/migration/V20260601046:PK������+����Ψһ����uk_code(WHERE is_deleted=false)+2�����⻧��������+3��ҵ���ѯ����+�ع��ű� | fc064777 |
| P0-003-004-013-001-003 | ��֤��дprod_standard_process��׼�����DDL | 2026-06-01T22:40 | ? | db/migration/V20260601047:20����֤��ѯSQL+docs/verification:��֤����(68��ȫPASS) | 5afac150 |
| P0-003-004-014-001-001 | ��дCREATE TABLE��� | 2026-06-01T22:45 | ? | db/migration/V20260601048:CREATE TABLE prod_product_price(12ҵ���ֶ�+10ͨ���ֶ�+COMMENT,decimal(18,8)ͳһ����)+�ع��ű� | 7abebbbc |
| P0-003-004-014-001-002 | ��дprod_product_price��������Լ�� | 2026-06-01T22:55 | ? | db/migration/V20260601049:PK������+����Ψһ����uk_code(WHERE is_deleted=false)+2�����⻧��������+9��ҵ���ѯ����+�ع��ű� | 083d6954 |
| P0-003-004-014-001-003 | ��֤DDL | 2026-06-01T23:00 | ? | db/migration/V20260601050:20����֤��ѯSQL+docs/verification/prod_product_price_verify_report.md:12����֤����(22�ֶ�+12����+100%COMMENT+decimal(18,8)+�����) | fbfd42ce |
| P0-003-004-015-001-001 | ��дCREATE TABLE��� | 2026-06-01T22:50 | ? | db/migration/V20260601051:CREATE TABLE prod_product_attribute(6ҵ���ֶ�+10ͨ���ֶ�+COMMENT)+�ع�;V20260601052:CREATE TABLE prod_product_attribute_value(5ҵ���ֶ�+10ͨ���ֶ�+COMMENT)+�ع� | 767731f2 |
| P0-003-004-015-001-002 | ��д������Լ�� | 2026-06-01T23:00 | ? | db/migration/V20260601053:PK������+����Ψһ����uk_code(WHERE is_deleted=false)+2�����⻧��������+3��ҵ���ѯ����+�ع��ű� | 572a31b0 |
| P0-003-004-015-001-003 | ��֤DDL | 2026-06-01T23:10 | ? | db/migration/V20260526001__verify_prod_product_attribute.sql:23����֤��ѯ��+docs/verification/prod_product_attribute_verify_report.md:8����֤����(2��31�ֶ�+7����+100%COMMENT+�����) | 357ac879 |
| P0-003-004-016-001-001 | ��дCREATE TABLE��� | 2026-06-01T23:20 | ? | db/migration/V20260601054:CREATE TABLE prod_product_spec(6ҵ���ֶ�+10ͨ���ֶ�+COMMENT)+�ع��ű� | adb3d3a1 |
| P0-003-004-016-001-002 | ��д������Լ�� | 2026-06-01T23:30 | ? | db/migration/V20260601055:PK������+����Ψһ����uk_code(WHERE is_deleted=false)+2�����⻧��������+3��ҵ���ѯ����+�ع��ű� | 2f5d8430 |
| P0-003-004-016-001-003 | ��֤DDL | 2026-06-01T23:45 | ? | db/migration/V20260601056:19����֤��ѯSQL+docs/verification/prod_product_spec_verify_report.md:10����֤����(16�ֶ�+7����+100%COMMENT������+ȫ���״���ʾ���,��̬���ȫPASS) | 262139d7 |
| P0-003-004-017-001-001 | ��дCREATE TABLE��� | 2026-06-01T23:55 | ? | db/migration/V20260601057:CREATE TABLE prod_product_barcode(7ҵ���ֶ�+10ͨ���ֶ�+COMMENT)+�ع��ű� | 48a2947f |
| P0-003-004-017-001-002 | ��д������Լ�� | 2026-06-02T00:10 | ? | db/migration/V20260601058:8������(PK������+1Ψһ������WHERE is_deleted=false+2���⻧+4ҵ���ѯ)+�ع��ű� | 9095e6f8 |
| P0-003-004-017-001-003 | ��֤DDL | 2026-06-02T00:20 | ? | db/migration/V20260601059:14����֤��ѯSQL+docs/verification/prod_product_barcode_verify_report.md:10����֤����(17�ֶ�+8����+100%COMMENT������+ȫ���״���ʾ���+����һ������֤PASS+��̬���ȫPASS) | �� |
| P0-003-004-018-001-001 | ��дCREATE TABLE��� | 2026-06-02T00:35 | ? | db/migration/V20260601060:CREATE TABLE prod_product_image(10ҵ���ֶ�+10ͨ���ֶ�+COMMENT)+�ع��ű� | �� |
| P0-003-004-018-001-002 | ��д������Լ�� | 2026-06-02T00:45 | ? | db/migration/V20260601061:8������(PK������+1Ψһ������WHERE is_deleted=false+2���⻧+5ҵ���ѯ)+�ع��ű� | 596298f5 |
| P0-003-004-018-001-003 | ��֤DDL | 2026-06-02T01:00 | ? | db/migration/V20260601062__verify_prod_product_image.sql(19����֤SQL)+docs/verification/prod_product_image_verify_report.md(5/5����ͨ��,0��CRITICAL,0��WARNING) | 20a51f05 |
| P0-003-004-019-001-001 | ��дCREATE TABLE��� | 2026-06-02T01:10 | ? | db/migration/V20260601063:CREATE TABLE prod_product_relation(8ҵ���ֶ�+10ͨ���ֶ�+COMMENT)+�ع��ű� | 8d6ba0ea |
| P0-003-004-019-001-002 | ��д������Լ�� | 2026-06-02T01:32 | ? | db/migration/V20260601064:8������(PK������+1Ψһ������WHERE is_deleted=false+2���⻧+5ҵ���ѯ)+�ع��ű� | 66b20582 |
| P0-003-004-019-001-003 | ��֤DDL | 2026-06-02T01:45 | ? | db/migration/V20260601065__verify_prod_product_relation.sql(19����֤SQL)+docs/verification/prod_product_relation_verify_report.md(5/5����ͨ��,0��CRITICAL,0��WARNING) | 7f9e4f6b |
| P0-003-004-020-001-001 | ��дCREATE TABLE��� | 2026-06-02T02:15 | ? | db/migration/V20260601066:CREATE TABLE prod_product_tag(4ҵ���ֶ�+10ͨ���ֶ�+COMMENT)+�ع��ű� | 21a98cf3 |
| P0-003-004-020-001-002 | ��д������Լ�� | 2026-06-02T02:40 | ? | db/migration/V20260601067:6������(PK������+1����Ψһ������WHERE is_deleted=false+1���⻧+3ҵ���ѯ)+�ع��ű� | a7e0e566 |
| P0-003-004-020-001-003 | ��֤DDL | 2026-06-02T03:05 | ? | db/migration/V20260526001__verify_prod_product_tag.sql(15����֤SQL)+docs/verification/prod_product_tag_verify_report.md(5/5����ͨ��) | TBD |
| P0-003-004-021-001-001 | ��дCREATE TABLE��� | 2026-06-02T03:20 | ? | db/migration/V20260601068:CREATE TABLE prod_serial_template(8ҵ���ֶ�+10ͨ���ֶ�+COMMENT)+�ع��ű� | 05f4d5f6 |
| P0-003-004-021-001-002 | ��д������Լ�� | 2026-06-02T03:30 | ? | db/migration/V20260601069:6������(PK������+1����Ψһ������WHERE is_deleted=false+2���⻧+2ҵ���ѯ)+�ع��ű� | 2a1fd617 |
| P0-003-004-021-001-003 | ��֤DDL | 2026-06-02T03:50 | ? | db/migration/V20260526001__verify_prod_serial_template.sql(15����֤SQL)+docs/verification/prod_serial_template_verify_report.md(5/5����ͨ��) | 6e99e387 |
| P0-003-004-022-001-001 | ��дCREATE TABLE��� | 2026-06-02T04:00 | ? | db/migration/V20260601070:CREATE TABLE prod_product_other(17ҵ���ֶ�+10ͨ���ֶ�+COMMENT)+�ع��ű� | 01f56cd6 |
| P0-003-004-022-001-002 | ��д������Լ�� | 2026-06-02T04:30 | ? | db/migration/V20260601071:6������(PK������+1����Ψһ������WHERE is_deleted=false+2���⻧+3ҵ���ѯ)+�ع��ű� | d68a5aeb |
| P0-003-004-022-001-003 | ��֤DDL | 2026-06-02T05:00 | ? | db/migration/V20260526001__verify_prod_product_other.sql(16����֤SQL)+docs/verification/prod_product_other_verify_report.md(ȫ��PASS, 0CRITICAL, 0WARNING, COMMENT������100%) | 0d004fc1 |
| P0-003-005-001-001-001 | ��дCREATE TABLE��� | 2026-06-02T05:10 | ? | db/migration/V20260601072:CREATE TABLE crm_customer_class(3ҵ���ֶ�+10ͨ���ֶ�+COMMENT)+�ع��ű� | 57aae787 |
| P0-003-005-001-001-002 | ��дcrm_customer_class�ͻ������������Լ�� | 2026-06-02T05:20 | ? | db/migration/V20260601073:PK������+����Ψһ����(WHERE is_deleted=false)+���⻧��������+ҵ���ѯ����+�ع��ű� | 8fd36f98 |
| P0-003-005-001-001-003 | ��֤��дcrm_customer_class�ͻ������DDL | 2026-06-02T05:30 | ? | db/migration/V20260601074:15����֤SQL(������/�ֶ�/ͨ���ֶ�/����/PK/Ψһ����/���⻧/COMMENT/NOT NULL/Flyway/���/����һ����)+docs/verification/crm_customer_class_verify_report.md(0CRITICAL, 1WARNING��չ�ֶ�COMMENT) | 639fba91 |
| P0-003-005-002-001-001 | ��дCREATE TABLE��� | 2026-06-02T05:40 | ? | db/migration/V20260601075:CREATE TABLE crm_tag_definition(5ҵ���ֶ�+10ͨ���ֶ�+COMMENT)+�ع��ű� | b910d4d7 |
| P0-003-005-002-001-002 | ��дCRM��ǩ�����������Լ�� | 2026-06-02T05:50 | ? | db/migration/V20260601076:PK������+����Ψһ����(WHERE is_deleted=false)+���⻧��������(tenant_id����)+ҵ���ѯ����+�ع��ű� | bf3f9cd3 |
| P0-003-005-002-001-003 | ��֤��дCRM��ǩ�����DDL | 2026-06-02T06:00 | ? | db/migration/V20260526001__task_P0_003_005_002_001_003.sql:14����֤SQL+docs/specs/P0_003_005_002_001_003_spec.md(ȫ��PASS) | 2e675026 |
| P0-003-005-003-001-001 | ��дCREATE TABLE��� | 2026-06-01T19:07 | ? | db/migration/V20260601077:CREATE TABLE crm_customer(6ҵ���ֶ�+10ͨ���ֶ�+COMMENT)+�ع��ű� | bbddd272 |
| P0-003-005-003-001-002 | ��дcrm_customer�ͻ�����������Լ�� | 2026-06-01T19:05 | ? | db/migration/V20260601078:PK������+����Ψһ����(WHERE is_deleted=false)+���⻧��������(tenant_id����)+ҵ���ѯ����+�ع��ű� | 387cf55e |
| P0-003-005-003-001-003 | ��֤��дcrm_customer�ͻ�����DDL | 2026-06-01T19:18 | ? | db/migration/V20260601079:15����֤SQL+docs/verification/crm_customer_verify_report.md(ȫ��PASS) | a5bb8c0b |
| P0-003-005-004-001-001 | ��дCREATE TABLE��� | 2026-06-01T19:15 | ? | db/migration/V20260601080:CREATE TABLE crm_contact_comm(9ҵ���ֶ�+10ͨ���ֶ�+22��չ�ֶ�+COMMENT+decimal(18,8)����) | 55fb5300 |
| P0-003-005-004-001-002 | ��дcrm_contact_comm�ͻ���ϵ�˱�������Լ�� | 2026-06-01T19:12 | ? | db/migration/V20260601081:PK������+����Ψһ����(WHERE is_deleted=false)+���⻧��������(tenant_id����)+ҵ���ѯ����(contact_id/parent_id/comm_type/ͨ���ֶ�/����)+�ع��ű� | ff69f3f2 |
| P0-003-005-004-001-003 | ��֤��дcrm_contact_comm�ͻ���ϵ�˱�DDL | 2026-06-01T19:35 | ? | db/migration/V20260526001:11����֤��ѯSQL+docs/verification/crm_contact_comm_verify_report.md(36�ֶ�+12����+100%COMMENT+10ͨ���ֶ�+decimal(18,8)+ȫ���״���ʾͨ��) | (����д) |
| P0-003-005-005-001-001 | ��дCREATE TABLE��� | 2026-06-01T19:20 | ? | db/migration/V20260601082:CREATE TABLE crm_customer_address(5ҵ���ֶ�+10ͨ���ֶ�+22��չ�ֶ�+COMMENT+decimal(18,8)����)+�ع��ű� | (����д) |
| P0-003-005-005-001-002 | ��дcrm_customer_address�ͻ���ַ��������Լ�� | 2026-06-01T20:08 | ? | db/migration/V20260601083:PK������+���⻧��������(tenant_id����)+ҵ���ѯ����(customer_id/address_type/city/ͨ���ֶ�/����)+�ع��ű� | (����д) |
| P0-003-005-005-001-003 | ��֤��дcrm_customer_address�ͻ���ַ��DDL | 2026-06-01T20:40 | ? | db/migration/V20260601084:15����֤��ѯSQL+docs/verification/crm_customer_address_verify_report.md(38�ֶ�+11����+42.1%COMMENT+10ͨ���ֶ�+decimal(18,8)+0 CRITICAL����) | cf712779 |
| P0-003-005-006-001-001 | ��дCREATE TABLE��� | 2026-06-01T21:00 | ? | db/migration/V20260601085:CREATE TABLE crm_customer_tag_rel(4ҵ���ֶ�+10ͨ���ֶ�+22��չ�ֶ�+COMMENT+decimal(18,8)����)+�ع��ű� | 474d9c2a |
| P0-003-005-006-001-002 | ��дcrm_customer_tag_rel�ͻ���ǩ������������Լ�� | 2026-06-01T21:10 | ? | db/migration/V20260601086:PK������+����Ψһ����(WHERE is_deleted=false)+���⻧��������(tenant_id����)+ҵ���ѯ����(customer_id/tag_id/ͨ���ֶ�/����)+�ع��ű� | 418d9d6ef |
| P0-003-005-006-001-003 | ��֤��дcrm_customer_tag_rel�ͻ���ǩ������DDL | 2026-06-01T21:25 | ? | db/migration/V20260601087:18����֤��ѯSQL+docs/verification/crm_customer_tag_rel_verify_report.md(ȫ��PASS,0 CRITICAL) | 765970f1 |
| P0-003-005-007-001-001 | ��дCREATE TABLE��� | 2026-06-01T20:50 | ? | db/migration/V20260601088: crm_customer_attachment�ͻ�������CREATE TABLE+COMMENT+rollback | b257296b |
| P0-003-005-007-001-002 | ��дcrm_customer_attachment�ͻ�������������Լ�� | 2026-06-01T21:40 | ? | db/migration/V20260601089: PK������+����Ψһ����(WHERE is_deleted=false)+���⻧��������(tenant_id����)+ҵ���ѯ����(customer_id/ͨ���ֶ�/����)+�ع��ű� | 7788e438 |
| P0-003-005-007-001-003 | ��֤��дcrm_customer_attachment�ͻ�������DDL | 2026-06-01T20:20 | ? | db/migration/V20260601090:18����֤��ѯSQL+docs/verification/crm_customer_attachment_verify_report.md(ȫ��PASS,0 CRITICAL) | (����д) |
| P0-003-005-008-001-001 | ��дCREATE TABLE��� | 2026-06-01T21:40 | ? | db/migration/V20260601091:CREATE TABLE crm_customer_evaluation(11ҵ���ֶ�+10ͨ���ֶ�+22��չ�ֶ�+COMMENT+decimal(18,8)����)+�ع��ű� | (����д) |
| P0-003-005-008-001-002 | ��дcrm_customer_evaluation�ͻ����۱�������Լ�� | 2026-06-01T21:50 | ? | db/migration/V20260601092: PK������+����Ψһ����(WHERE is_deleted=false)+���⻧��������(tenant_id����)+ҵ���ѯ����(customer_id/parent_id/evaluator_id/ͨ���ֶ�/����)+�ع��ű� | 87afc516 |
| P0-003-005-008-001-003 | ��֤��дcrm_customer_evaluation�ͻ����۱�DDL | 2026-06-01T22:00 | ? | db/migration/V20260601093:18����֤��ѯSQL+docs/verification/crm_customer_evaluation_verify_report.md(ȫ��PASS,0 CRITICAL) | (����д) |
| P0-003-005-009-001-001 | ��дCREATE TABLE��� | 2026-06-01T22:10 | ? | db/migration/V20260601094:CREATE TABLE crm_customer_finance(26ҵ���ֶ�+10ͨ���ֶ�+22��չ�ֶ�+COMMENT+decimal(18,8)����)+�ع��ű� | d5c91011 |
| P0-003-005-009-001-002 | ��дcrm_customer_finance�ͻ��������ñ�������Լ�� | 2026-06-01T22:20 | ? | db/migration/V20260601095: PK������+����Ψһ����(WHERE is_deleted=false)+���⻧��������(tenant_id����)+ҵ���ѯ����(customer_id/product_id/order_date/ͨ���ֶ�)+�ع��ű� | ea246012 |
| P0-003-005-009-001-003 | ��֤��дcrm_customer_finance�ͻ��������ñ�DDL | 2026-06-01T22:35 | ? | db/migration/V20260601096:18����֤��ѯSQL+docs/verification/crm_customer_finance_verify_report.md(ȫ��PASS) | 6a28e579 |
| P0-003-005-010-001-001 | ��дCREATE TABLE��� | 2026-06-01T23:00 | ? | db/migration/V20260601097:CREATE TABLE crm_opportunity(11ҵ���ֶ�+10ͨ���ֶ�+22��չ�ֶ�+COMMENT+decimal(18,8)����)+�ع��ű� | e1b4a640 |
| P0-003-005-010-001-002 | ��дcrm_opportunity�ͻ������������Լ�� | 2026-06-01T23:15 | ? | db/migration/V20260601098:PK������(pk_crm_opportunity)+����Ψһ����uk_crm_opportunity_code(WHERE is_deleted=false)+6�����⻧��������(tenant_id����)+8��ҵ���ѯ����+�ع��ű� | 820e6d8e |
| P0-003-005-010-001-003 | ��֤��дcrm_opportunity�ͻ������DDL | 2026-06-01T23:30 | ? | db/migration/V20260601099:18����֤��ѯSQL+docs/verification/crm_opportunity_verify_report.md(ȫ��PASS) | 113f267d |
| P0-003-005-011-001-001 | ��дCREATE TABLE��� | 2026-06-01T23:45 | ? | db/migration/V20260601100:CREATE TABLE crm_project(8ҵ���ֶ�+10ͨ���ֶ�+22��չ�ֶ�+COMMENT+decimal(18,8)����)+�ع��ű� | a7ab1b5b |
| P0-003-005-011-001-002 | ��дcrm_project�ͻ���Ŀ��������Լ�� | 2026-06-02T00:00 | ? | db/migration/V20260601101:PK������(pk_crm_project)+����Ψһ����uk_crm_project_code(WHERE is_deleted=false)+6�����⻧��������(tenant_id����)+8��ҵ���ѯ����+�ع��ű� | bfdaccee |
| P0-003-005-011-001-003 | ��֤��дcrm_project�ͻ���Ŀ��DDL | 2026-06-02T06:30 | ? | db/migration/V20260601102:18����֤��ѯSQL+docs/verification/crm_project_verify_report.md:10����֤����(57�ֶ�+20����+100%COMMENT������+ȫ���״���ʾ���+��̬���ȫPASS) | 16cdd75a |
| P0-003-006-001-001-001 | ��дCREATE TABLE��� | 2026-06-01T20:30 | ? | db/migration/V20260601103:srm_supplier_class��Ӧ�̷����DDL(10ͨ���ֶ�+3ҵ���ֶ�+��չ�ֶ�+COMMENT)+�ع��ű� | 46e28907 |
| P0-003-006-001-001-002 | ��дsrm_supplier_class��Ӧ�̷����������Լ�� | 2026-06-02T07:00 | ? | db/migration/V20260601104:����status��+PK������(pk_srm_supplier_class)+����Ψһ����uk(WHERE is_deleted=false)+3���⻧��������+6ҵ���ѯ����+�ع��ű� | (pending) |
| P0-003-006-001-001-003 | ��֤��дsrm_supplier_class��Ӧ�̷����DDL | 2026-06-02T10:30 | ? | db/migration/V20260601105__verify_srm_supplier_class.sql(18����֤SQL)+docs/verification/srm_supplier_class_verify_report.md(ȫ��PASS, 0CRITICAL, 0WARNING, COMMENT������100%) | (pending) |
| P0-003-006-002-001-001 | ��дCREATE TABLE��� | 2026-06-02T12:00 | ? | db/migration/V20260602001:CREATE TABLE srm_tag_definition(5ҵ���ֶ�+10ͨ���ֶ�+22��չ�ֶ�+COMMENT+decimal(18,8))+spec�ĵ� | (pending) |
| P0-003-006-002-001-002 | ��дSRM��ǩ�����������Լ�� | 2026-06-02T13:00 | ? | db/migration/V20260602002:PK������(pk_srm_tag_definition)+����Ψһ����uk(WHERE is_deleted=false)+2���⻧��������+3ҵ���ѯ����+5ͨ���ֶ�����+spec�ĵ� | (pending) |
| P0-003-006-002-001-003 | ��֤��дSRM��ǩ�����DDL | 2026-06-02T14:00 | ? | db/migration/V20260602003:17����֤SQL(��/�ֶ�/Լ��/����/COMMENT/Flyway/���)+docs/specs/P0_003_006_002_001_003_spec.md(��֤�淶�ĵ�) | (pending) |
| P0-003-006-003-001-001 | ��дCREATE TABLE��� | 2026-06-02T15:00 | ? | db/migration/V20260602004:CREATE TABLE srm_supplier(5ҵ���ֶ�+10ͨ���ֶ�+22��չ�ֶ�+COMMENT+decimal(18,8))+�ع��ű� | (pending) |
| P0-003-006-003-001-002 | ��дsrm_supplier��Ӧ������������Լ�� | 2026-06-02T16:00 | ? | db/migration/V20260602005:PK������(pk_srm_supplier)+����Ψһ����uk(WHERE is_deleted=false)+3���⻧��������+6ҵ���ѯ����+�ع��ű� | (pending) |
| P0-003-006-003-001-003 | ��֤��дsrm_supplier��Ӧ������DDL | 2026-06-02T17:00 | ? | db/migration/V20260602006:20����֤SQL(��/�ֶ�/Լ��/����/COMMENT/Flyway/���/����/����)+docs/verification/srm_supplier_verify_report.md(37��ȫPASS) | 3575917a |
| P0-003-006-004-001-001 | ��дCREATE TABLE��� | 2026-06-02T17:30 | ? | db/migration/V20260602007:CREATE TABLE srm_supplier_comm��Ӧ����ϵ��ͨѶ��(9ҵ���ֶ�+10ͨ���ֶ�+22��չ�ֶ�+COMMENT+decimal(18,8))+�ع��ű� | 60c41c94 |
| P0-003-006-004-001-002 | ��дsrm_supplier_comm��Ӧ����ϵ�˱�������Լ�� | 2026-06-02T17:40 | ? | db/migration/V20260602008:PK������+����Ψһ����(WHERE is_deleted=false)+���⻧��������(tenant_id����)+ҵ���ѯ����(FK/supplier_id/status/parent_id/����)+�ع��ű� | e9d8cb5a |
| P0-003-006-004-001-003 | ��֤��дsrm_supplier_comm��Ӧ����ϵ�˱�DDL | 2026-06-02T18:00 | ? | db/migration/V20260602009:11����֤SQL(��/�ֶ�/Լ��/����/COMMENT/Flyway/���/����/����/����/����)+docs/verification/srm_supplier_comm_verify_report.md(41��ȫPASS) | 12c6c5b3 |
| P0-003-006-005-001-001 | ��дCREATE TABLE��� | 2026-06-02T17:10 | ? | db/migration/V20260602010__create_srm_supplier_address.sql(38��:8ҵ��+20��չ+10ͨ��)+rollback�ű� | 6cadb4bd |
| P0-003-006-005-001-002 | ��дsrm_supplier_address��Ӧ�̵�ַ��������Լ�� | 2026-06-02T18:00 | ? | db/migration/V20260602011:PK������+����Ψһ����(WHERE is_deleted=false, supplier_id+address_type)+���⻧��������(tenant_id����)+ҵ���ѯ����(FK/supplier_id/status/����)+�ع��ű� | 2a22439a |
| P0-003-006-005-001-003 | ��֤��дsrm_supplier_address��Ӧ�̵�ַ��DDL | 2026-06-02T18:30 | ? | db/migration/V20260602012:11����֤SQL(��/�ֶ�/Լ��/����/COMMENT/Flyway/���/����/����/����/����)+docs/verification/srm_supplier_address_verify_report.md(40��ȫPASS) | �� |
| P0-003-006-006-001-001 | ��дCREATE TABLE��� | 2026-06-02T18:15 | ? | db/migration/V20260602013:CREATE TABLE srm_supplier_tag_rel(10ͨ���ֶ�+4ҵ���ֶ�(supplier_id/tag_id/code/status)+��չ�ֶ�)+rollback�ű� | 0b84473b |
| P0-003-006-006-001-002 | ��дsrm_supplier_tag_rel��Ӧ�̱�ǩ������������Լ�� | 2026-06-02T19:00 | ? | db/migration/V20260602014:PK������(pk_srm_supplier_tag_rel)+����Ψһ����uk(WHERE is_deleted=false)+2���⻧��������(tenant_id����)+8ҵ���ѯ����+�ع��ű� | 6af93b19 |
| P0-003-006-006-001-003 | ��֤��дsrm_supplier_tag_rel��Ӧ�̱�ǩ������DDL | 2026-06-02T19:20 | ? | db/migration/V20260602015:11����֤SQL(��/�ֶ�/ͨ���ֶ�/����/Լ��/COMMENT/Flyway/����/����Ψһ����/���⻧����/����)+docs/verification/srm_supplier_tag_rel_verify_report.md(7���״���ʾȫ�����) | f2885535 |
| P0-003-006-007-001-001 | ��дCREATE TABLE��� | 2026-06-02T19:30 | ? | db/migration/V20260602016__create_srm_supplier_attachment.sql(11ҵ���ֶ�+10ͨ���ֶ�+ȫCOMMENT) + V20260602016�ع��ű� | �� |
| P0-003-006-007-001-002 | ��дsrm_supplier_attachment��Ӧ�̸�����������Լ�� | 2026-06-02T17:06 | ? | db/migration/V20260602017:PK������(pk_srm_supplier_attachment)+����Ψһ����uk(WHERE is_deleted=false)+2���⻧��������(tenant_id����)+9ҵ���ѯ����+�ع��ű� | 32982f49 |
| P0-003-006-007-001-003 | ��֤��дsrm_supplier_attachment��Ӧ�̸�����DDL | 2026-06-02T19:45 | ? | db/migration/V20260602018:11����֤SQL(��/�ֶ�/ͨ���ֶ�/����/Լ��/COMMENT/Flyway/����/����Ψһ����/���⻧����/����)+docs/verification/srm_supplier_attachment_verify_report.md(7���״���ʾȫ�����) | 773d43a4 |
| P0-003-006-008-001-001 | ��дCREATE TABLE��� | 2026-06-02T20:00 | ? | db/migration/V20260602019:CREATE TABLE srm_supplier_evaluation(11ҵ���ֶ�+10ͨ���ֶ�+22��չ�ֶ�+ȫCOMMENT+decimal(18,8))+�ع��ű� | ebe3b5bb |
| P0-003-006-008-001-003 | ��֤��дsrm_supplier_evaluation��Ӧ�����۱�DDL | 2026-06-02T17:15 | ? | db/migration/V20260602021:��֤SQL(11��)+docs/verification����;DDL��̬���43�ֶ�/15����/20ע��/10ͨ���ֶ�ȫ��ͨ�� | �� |
| P0-003-006-009-001-001 | ��дCREATE TABLE��� | 2026-06-02T20:10 | ? | db/migration/V20260602022:CREATE TABLE srm_supplier_finance(5ҵ���ֶ�+ģ���ֶ�+22��չ�ֶ�+10ͨ���ֶ�+ȫCOMMENT+decimal(18,8))+�ع��ű� | d7200be6 |
| P0-003-006-009-001-002 | ��дsrm_supplier_finance��Ӧ�̲������ñ�������Լ�� | 2026-06-02T17:15 | ? | db/migration/V20260602023:PK������(pk_srm_supplier_finance)+����Ψһ����uk(WHERE is_deleted=false)+2���⻧��������(tenant_id����)+8ҵ���ѯ����+�ع��ű� | �� |
| P0-003-006-009-001-003 | ��֤��дsrm_supplier_finance��Ӧ�̲������ñ�DDL | 2026-06-02T20:30 | ? | db/migration/V20260602024:17����֤SQL(������/�ֶ�/ͨ���ֶ�/����/NOT NULL/PK/����Ψһ����/���⻧����/COMMENT/FK/Flyway/Ĭ��ֵ)+docs/verification/srm_supplier_finance_verify_report.md:8����������(5/5����ͨ��,��̬���ȫPASS) | 0286df4e |

### P0-003-007 - �ֿ��������

| ������ | �������� | ���ʱ�� | ״̬ | ժҪ | SHA |
|---------|---------|---------|:---:|------|:---:|
| P0-003-007-001-001-001 | ��дCREATE TABLE��� | 2026-06-02T17:10 | ? | db/migration/V20260526001:CREATE TABLE inv_warehouse(5ҵ���ֶ�+10ͨ���ֶ�+22��չ�ֶ�+ȫCOMMENT+decimal(18,8))+�ع��ű� | �� |
| P0-003-007-001-001-002 | ��дinv_warehouse�ֿⶨ���������Լ�� | 2026-06-02T17:10 | ? | db/migration/V20260526001:PK������+����Ψһ����uk_warehouse_code(WHERE is_deleted=false)+tenant_id��������+ҵ���ѯ����+ͨ���ֶ�����+�ع��ű� | �� |
| P0-003-007-001-001-003 | ��֤��дinv_warehouse�ֿⶨ���DDL | 2026-06-02T17:20 | ? | db/migration/V20260526001:15����֤SQL(������/�ֶ�/ͨ���ֶ�/����/PK/����Ψһ����/��������/���⻧����/COMMENT/NOT NULL/FK/����)+docs/verification/inv_warehouse_verify_report.md:4����������(14/14���ļ��ͨ��) | �� |
| P0-003-007-002-001-001 | ��дCREATE TABLE��� | 2026-06-02T17:20 | ? | db/migration/V20260526001:CREATE TABLE inv_location(5ҵ���ֶ�+10ͨ���ֶ�+22��չ�ֶ�+ȫCOMMENT+decimal(18,8))+�ع��ű� | �� |
| P0-003-007-002-001-002 | ��дinv_location��λ������������Լ�� | 2026-06-02T17:35 | ? | db/migration/V20260602001:PK������+����Ψһ����uk_inv_location_code(WHERE is_deleted=false)+tenant_id��������+ҵ���ѯ����+�ع��ű� | �� |
| P0-003-007-002-001-003 | ��֤��дinv_location��λ������DDL | 2026-06-02T17:45 | ? | db/migration/V20260526001:15����֤SQL+docs/verification/inv_location_verify_report.md:5����������(��������:Flyway�汾��ͻ+��չ�ֶ�COMMENTȱʧ) | �� |
| P0-003-007-003-001-001 | ��дCREATE TABLE��� | 2026-06-02T17:50 | ? | db/migration/V20260526001:CREATE TABLE inv_stock(22ҵ���ֶ�+10ͨ���ֶ�+22��չ�ֶ�+32��COMMENT+decimal(18,8))+�ع��ű� | �� |
| P0-003-007-003-001-002 | ��дinv_stock���ʵʱ��������Լ�� | 2026-06-02T18:00 | ? | db/migration/V20260526001:PK������+����Ψһ����uk_inv_stock_order_no(WHERE is_deleted=false)+tenant_id��������+ҵ���ѯ����(14������)+�ع��ű� | �� |
| P0-003-007-003-001-003 | ��֤��дinv_stock���ʵʱ��DDL | 2026-06-02T18:15 | ? | db/migration/V20260526001__verify_inv_stock.sql:15����֤SQL(������/�ֶ�/ͨ���ֶ�/����/PK/����Ψһ����/��������/���⻧����/COMMENT/NOT NULL/FK/����)+docs/verification/inv_stock_verify_report.md:5����������(1MEDIUM:22��չ�ֶ�ȱCOMMENT, 1WARN:Flyway�汾����) | �� |
| P0-003-007-004-001-001 | ��дCREATE TABLE��� | 2026-06-02T17:05 | ? | db/migration/V20260526001__create_inv_stock_location.sql:CREATE TABLE inv_stock_location(6ҵ���ֶ�+4�����ֶ�+11��Ʒ����+22��չ�ֶ�+10ͨ���ֶ�+ȫCOMMENT+decimal(18,8))+�ع��ű� | �� |
| P0-003-007-004-001-002 | ��дinv_stock_location��λ����������Լ�� | 2026-06-02T18:30 | ? | db/migration/V20260526001:PK������+����Ψһ����uk_inv_stock_location_unique(5�����WHERE is_deleted=false)+tenant_id��������+ҵ���ѯ����(17������)+�ع��ű� | �� |
| P0-003-007-004-001-003 | ��֤��дinv_stock_location��λ����DDL | 2026-06-02T19:00 | ? | db/migration/V20260526001__verify_inv_stock_location.sql:13����֤SQL+docs/verification/inv_stock_location_verify_report.md:5����������(1MEDIUM:22��չ�ֶ�ȱCOMMENT, 1WARN:Flyway�汾����) | 4d5f4365 |
| P0-003-007-005-001-001 | ��дinv_other_outbound_detail�����������ӱ�CREATE TABLE��� | 2026-06-02T14:52 | ? | db/migration/V20260526001:CREATE TABLE inv_other_outbound(14ҵ���ֶ�+10ͨ���ֶ�)+inv_other_outbound_detail(35ҵ���ֶ�+10ͨ���ֶ�)+ȫCOMMENT+decimal(18,8) | 6a2f442a |
| P0-003-007-005-001-002 | ��дinv_other_outbound_detail�����������ӱ�������Լ�� | 2026-06-02T17:20 | ? | db/migration/V20260526001:PK������+����Ψһ����uk_inv_other_outbound_detail_code(WHERE is_deleted=false)+tenant_id��������+ҵ���ѯ����(15������)+�ع��ű� | �� |
| P0-003-007-005-001-003 | ��֤��дinv_other_outbound_detail�����������ӱ�DDL | 2026-06-02T17:30 | ? | db/migration/V20260602001__verify_inv_other_outbound_detail.sql:22����֤SQL+docs/verification/inv_other_outbound_detail_verify_report.md:���ձ��� | �� |
| P0-003-007-006-001-001 | ��дinv_other_inbound_detail����������ӱ�CREATE TABLE��� | 2026-06-02T17:40 | ? | db/migration/V20260526001:CREATE TABLE inv_other_inbound(14ҵ���ֶ�+10ͨ���ֶ�)+inv_other_inbound_detail(35ҵ���ֶ�+10ͨ���ֶ�)+ȫCOMMENT+decimal(18,8) | �� |
| P0-003-007-006-001-002 | ��дinv_other_inbound_detail����������ӱ�������Լ�� | 2026-06-02T18:00 | ? | db/migration/V20260526001:PK������+����Ψһ����uk(WHERE is_deleted=false)+tenant_id��������+ҵ���ѯ����(����17��+�ӱ�15����32������)+�ع��ű� | �� |
| P0-003-007-006-001-003 | ��֤��дinv_other_inbound_detail����������ӱ�DDL | 2026-06-02T18:15 | ? | db/migration/V20260602002__verify_inv_other_inbound_detail.sql:26����֤SQL+docs/verification/inv_other_inbound_detail_verify_report.md:���ձ���(5��:ժҪ/�ṹ/����/�淶/���ն���) | �� |
| P0-003-007-007-001-001 | ��дCREATE TABLE��� | 2026-06-02T17:30 | ? | db/migration/V20260526001:CREATE TABLE inv_stocktaking(12ҵ���ֶ�+10ͨ���ֶ�)+inv_stocktaking_detail(26ҵ���ֶ�+10ͨ���ֶ�+22��չ�ֶ�)+ȫCOMMENT+decimal(18,8)+�ع��ű� | �� |
| P0-003-007-007-001-002 | ��дinv_stocktaking_detail�̵����ӱ�������Լ�� | 2026-06-02T17:00 | ? | db/migration/V20260526001:PK������+����Ψһ����uk(WHERE is_deleted=false)+tenant_id��������+ҵ���ѯ����(����17��+�ӱ�15����32������)+�ع��ű� | �� |
| P0-003-007-007-001-003 | ��֤��дinv_stocktaking_detail�̵����ӱ�DDL | 2026-06-02T18:00 | ? | db/migration/V20260526001__verify_inv_stocktaking_detail.sql:29����֤SQL(����14+�ӱ�14+Flyway+���ӹ���)+docs/verification/inv_stocktaking_detail_verify_report.md:5����������(1MEDIUM:22��չ�ֶ�ȱCOMMENT, 1WARN:Flyway�汾����) | �� |
| P0-003-007-008-001-001 | ��дCREATE TABLE��� | 2026-06-02T17:10 | ? | db/migration/V20260526001:CREATE TABLE inv_transfer(12ҵ���ֶ�+10ͨ���ֶ�)+inv_transfer_detail(26ҵ���ֶ�+10ͨ���ֶ�+22��չ�ֶ�)+ȫCOMMENT+decimal(18,8)+�ع��ű� | �� |
| P0-003-007-008-001-002 | ��дinv_transfer_detail�������ӱ�������Լ�� | 2026-06-02T17:05 | ? | db/migration/V20260526001:PK������+����Ψһ����uk(WHERE is_deleted=false)+tenant_id��������+ҵ���ѯ����(����18��+�ӱ�16����34������)+�ع��ű� | �� |
| P0-003-007-008-001-003 | ��֤��дinv_transfer_detail�������ӱ�DDL | 2026-06-02T17:30 | ? | db/migration/V20260526001__verify_inv_transfer_detail.sql:17����֤SQL(������/�ֶ�/ͨ���ֶ�/����/NOT NULL/PK/����Ψһ����/���⻧����/COMMENT/FK/Flyway/Ĭ��ֵ)+docs/verification/inv_transfer_detail_verify_report.md:5����������(1MEDIUM:22��չ�ֶ�ȱCOMMENT, 1WARN:Flyway�汾����) | �� |
| P0-003-007-009-001-001 | ��дCREATE TABLE��� | 2026-06-02T17:30 | ? | db/migration/V20260526001:CREATE TABLE inv_loss(12ҵ���ֶ�+10ͨ���ֶ�)+inv_loss_detail(26ҵ���ֶ�+10ͨ���ֶ�+22��չ�ֶ�)+ȫCOMMENT+decimal(18,8)+�ع��ű� | �� |
| P0-003-007-009-001-002 | ��дinv_loss_detail�������ӱ�������Լ�� | 2026-06-02T17:45 | ? | db/migration/V20260526001:PK������(inv_loss+inv_loss_detail)+����Ψһ����uk(WHERE is_deleted=false)+tenant_id��������+ҵ���ѯ����(����17��+�ӱ�15����32������)+�ع��ű� | �� |
| P0-003-007-009-001-003 | ��֤��дinv_loss_detail�������ӱ�DDL | 2026-06-02T16:03 | ? | db/migration/V20260526001__verify_inv_loss_detail.sql:17����֤SQL(2��/����columns/PK/����Ψһ����/�����б�/���⻧����/COMMENT/NOT NULL/FK/����/����)+docs/verification/inv_loss_detail_verify_report.md:6����������(��֤��Χ/�ṹ��֤/����Լ��/�淶�Ϲ�/�״�����/�����ܽ�) | 7c55c82c |
| P0-003-007-010-001-001 | ��дCREATE TABLE��� | 2026-06-03T10:00 | ? | db/migration/V20260526001:CREATE TABLE inv_overflow(12ҵ���ֶ�+10ͨ���ֶ�)+inv_overflow_detail(26ҵ���ֶ�+10ͨ���ֶ�+22��չ�ֶ�)+ȫCOMMENT+decimal(18,8)+�ع��ű� | �� |
| P0-003-007-010-001-002 | ��дinv_overflow_detail�������ӱ�������Լ�� | 2026-06-03T11:00 | ? | db/migration/V20260526001:PK������(inv_overflow+inv_overflow_detail)+����Ψһ����uk(WHERE is_deleted=false)+tenant_id��������+ҵ���ѯ����(����16��+�ӱ�16����32������)+�ع��ű� | �� |
| P0-003-007-010-001-003 | ��֤��дinv_overflow_detail�������ӱ�DDL | 2026-06-03T17:45 | ? | db/migration/V20260526001__verify_inv_overflow_detail.sql:17����֤SQL(2��/����columns/PK/����Ψһ����/�����б�/���⻧����/COMMENT/NOT NULL/FK/����/����)+docs/verification/inv_overflow_detail_verify_report.md:6����������(��֤��Χ/�ṹ��֤/����Լ��/�淶�Ϲ�/�״�����/�����ܽ�) | �� |
| P0-003-007-011-001-001 | ��дCREATE TABLE��� | 2026-06-03T17:50 | ? | db/migration/V20260603001:CREATE TABLE inv_assembly(12ҵ���ֶ�+10ͨ���ֶ�)+inv_assembly_detail(26ҵ���ֶ�+10ͨ���ֶ�+22��չ�ֶ�)+ȫCOMMENT+decimal(18,8)+�ع��ű� | �� |
| P0-003-007-011-001-002 | ��дinv_assembly_detail��װ���ӱ�������Լ�� | 2026-06-03T17:56 | ? | db/migration/V20260603002:PK������(inv_assembly+inv_assembly_detail)+����Ψһ����uk(WHERE is_deleted=false)+tenant_id��������+ҵ���ѯ����(����16��+�ӱ�16����32������)+�ع��ű� | �� |
| P0-003-007-011-001-003 | ��֤��дinv_assembly_detail��װ���ӱ�DDL | 2026-06-03T18:05 | ? | db/migration/V20260526001__verify_inv_assembly_detail.sql:17����֤SQL(2��/columns/PK/����Ψһ����/�����б�/���⻧����/COMMENT/NOT NULL/FK/DECIMAL����/�����ֶ�/��չ�ֶ�)+docs/verification/inv_assembly_detail_verify_report.md:6���������� | �� |
| P0-003-007-012-001-001 | ��дCREATE TABLE��� | 2026-06-03T18:05 | ? | db/migration/V20260603003:CREATE TABLE inv_disassembly(12ҵ���ֶ�+10ͨ���ֶ�)+inv_disassembly_detail(26ҵ���ֶ�+10ͨ���ֶ�+22��չ�ֶ�)+ȫCOMMENT+decimal(18,8)+�ع��ű� | �� |
| P0-003-007-012-001-002 | ��дinv_disassembly_detail��ж���ӱ�������Լ�� | 2026-06-03T18:20 | ? | db/migration/V20260603004:PK������(inv_disassembly+inv_disassembly_detail)+����Ψһ����uk(WHERE is_deleted=false)+tenant_id��������+ҵ���ѯ����(����16��+�ӱ�16����32������)+�ع��ű� | �� |

### P0-003-008 - �������������

| ������ | �������� | ���ʱ�� | ״̬ | ժҪ | SHA |
|---------|---------|---------|:---:|------|-----|
| P0-003-008-001-001-001 | ��дCREATE TABLE��� | 2026-06-03T18:24 | ? | db/migration/V20260526001:CREATE TABLE fin_currency_rate(4ҵ���ֶ�+10ͨ���ֶ�+22��չ�ֶ�+ȫCOMMENT+decimal(18,8))+�ع��ű� | �� |
| P0-003-008-001-001-002 | ��дfin_currency_rate���ֻ��ʱ�������Լ�� | 2026-06-03T18:35 | ? | db/migration/V20260603001:PK������pk_fin_currency_rate+����Ψһ����uk_fin_currency_rate_currency_effective(WHERE is_deleted=false)+5���⻧��������(tenant_id����)+7��ҵ���ѯ����+�ع��ű� | �� |
| P0-003-008-001-001-003 | ��֤��дfin_currency_rate���ֻ��ʱ�DDL | 2026-06-03T18:40 | ? | db/migration/V20260603002:fin_currency_rate��DDL��֤��ѯ�ű�(14����)+docs/verification/fin_currency_rate_verify_report.md(��֤����:ͨ��/1��������warning) | �� |
| P0-003-008-002-001-001 | ��дCREATE TABLE��� | 2026-06-03T18:50 | ? | db/migration/V20260603006:CREATE TABLE fin_bank_account(4ҵ���ֶ�+10ͨ���ֶ�+22��չ�ֶ�+ȫCOMMENT+decimal(18,8))+�ع��ű� | �� |
| P0-003-008-002-001-002 | ��дfin_bank_account�����˻���������Լ�� | 2026-06-03T19:02 | ? | db/migration/V20260603007:PK������pk_fin_bank_account+����Ψһ����uk_fin_bank_account_account_no(WHERE is_deleted=false)+2���⻧��������+3��ҵ���ѯ����+5��ͨ���ֶ�����+�ع��ű� | �� |
| P0-003-008-002-001-003 | ��֤��дfin_bank_account�����˻���DDL | 2026-06-03T19:00 | ? | db/migration/V20260603008:fin_bank_account��DDL��֤��ѯ�ű�(14����)+docs/verification/fin_bank_account_verify_report.md(��֤����:ͨ��/1��������warning, COMMENT������100%) | �� |
| P0-003-008-003-001-001 | ��дfin_account��ƿ�Ŀ��CREATE TABLE��� | 2026-06-03T19:05 | ? | db/migration/V20260603009:fin_account��DDL+rollback(10ͨ���ֶ�+5ҵ���ֶ�+��չ�ֶ�+COMMENTȫ����) | db1898be |
| P0-003-008-003-001-002 | ��дfin_account��ƿ�Ŀ��������Լ�� | 2026-06-03T19:25 | ? | db/migration/V20260526001:PK������pk_fin_account+����Ψһ����uk_fin_account_code(WHERE is_deleted=false)+2���⻧��������+4��ҵ���ѯ����+5��ͨ���ֶ�����+�ع��ű� | 959d76fc |
| P0-003-008-003-001-003 | ��֤��дfin_account��ƿ�Ŀ��DDL | 2026-06-03T19:40 | ? | db/migration/V20260604001:fin_account��DDL��֤��ѯ�ű�(6���ѯ=��/��/����/Լ��/ע��/Flyway)+docs/verification/fin_account_verify_report.md(8��淶�Ϲ���ȫ��PASS,1��������WARNING) | �� |
| P0-003-008-004-001-001 | ��дfin_voucher_wordƾ֤�ֱ�CREATE TABLE��� | 2026-06-03T19:45 | ? | db/migration/V20260604002:fin_voucher_word��DDL+rollback(10ͨ���ֶ�+3ҵ���ֶ�+COMMENTȫ����) | 5137eaa3 |
| P0-003-008-004-001-002 | ��дfin_voucher_wordƾ֤�ֱ�������Լ�� | 2026-06-03T20:00 | ? | db/migration/V20260604003:PK������pk_fin_voucher_word+����Ψһ����uk_fin_voucher_word_code(WHERE is_deleted=false)+2���⻧��������+6��ҵ���ѯ����+�ع��ű� | b344d20b |
| P0-003-008-004-001-003 | ��֤��дfin_voucher_wordƾ֤�ֱ�DDL | 2026-06-03T20:10 | ? | db/migration/V20260604004:fin_voucher_word��DDL��֤��ѯ�ű�(6���ѯ=��/��/����/Լ��/ע��/Flyway)+docs/verification/fin_voucher_word_verify_report.md(7���״���ʾ�˲�ȫ��PASS) | �� |

---

---


---

### ģ�����: P0-003 ?

### P0-004 - ��֤��Ȩ�޻�������

| ������ | �������� | ���ʱ�� | ״̬ | ժҪ | SHA |
|---------|---------|---------|:---:|------|-----|
| P0-004-001-001-001-001 | ��д���Ĵ��� | 2026-06-03T17:36 | ? | ʵ�ֵ�¼��֤�����߼�: AuthController/AuthService/CaptchaService/LoginRequest/LoginResponse/SysUser/SysLoginLog | 8cf84b5e |
| P0-004-001-001-001-002 | ��֤���� | 2026-06-03T17:48 | ? | ��д27����Ԫ����(AuthServiceTest/CaptchaServiceTest/AuthControllerTest)��ȫ��ͨ�� | 08b8b70f |
| P0-004-001-002-001-001 | ��д���Ĵ��� | 2026-06-03T18:05 | ? | ʵ���˳���¼�����߼�: AuthController.logout/AuthService.logout/AuthOnlineDeviceʵ��/AuthOnlineDeviceMapper/LoginLogService.updateLogoutTime/SysLoginLog.logoutAt | a7d406d9 |
| P0-004-001-002-001-002 | ��֤���� | 2026-06-03T18:05 | ? | ��д6���˳���¼��Ԫ����(AuthServiceTest$Logout/AuthControllerTest$Logout)��ȫ��ͨ�������ɲ��Ա��� | �� |
| P0-004-001-003-001-001 | ��д���Ĵ��� | 2026-06-03T18:30 | ? | ʵ��TokenУ����ˢ��: TokenVerifyResponse/TokenRefreshResponse/TokenRefreshRequest/AuthService.verifyToken/refreshToken/AuthController�����¶˵� | 1c51e4c8 |
| P0-004-001-003-001-002 | ��֤���� | 2026-06-03T18:30 | ? | ��д12��TokenУ����ˢ�µ�Ԫ����(AuthServiceTest$VerifyToken/RefreshToken + AuthControllerTest$VerifyToken/RefreshToken)��34/34ͨ�������ɲ��Ա����������嵥 | �� |
| P0-004-001-004-001-001 | ��д�����ļ��� | 2026-06-03T18:35 | ? | src/main/java/com/erp/auth/config/AuthProperties.java:��֤ģ������������(��֤��/��¼��ȫ/�û���Ϣ����Ƕ������), @ConfigurationProperties(prefix="auth")+@ValidatedУ��; application.yml����auth���ö� | �� |
| P0-004-001-004-001-002 | ��֤��д���������� | 2026-06-03T18:42 | ? | ��дAuthPropertiesTest(15������):��֤���ð�/Ĭ��ֵ/Beanע��/@ValidatedУ��Լ��,ȫ��ͨ��;ȷ��4������YAML��Ӳ����������Ϣ | �� |
| P0-004-001-005-001-001 | ��д���Ĵ��� | 2026-06-03T19:00 | ? | ��¼��־����ʵ��: IpAddressUtil(����IP����)/UserAgentUtil(�����+OS����)/AsyncConfig(ThreadPoolTaskExecutor coreSize=2,maxSize=5,queueCapacity=1000)/LoginLogService.asyncWriteLog/AuthService�ع�ʹ���¹����� | �� |
| P0-004-001-005-001-002 | ��֤���� | 2026-06-03T19:00 | ? | ��֤��¼��־�첽д��: ����ͨ��/22������ȫ��ͨ��/������鸲���첽����/��������/�߽�����/IP-UA����/���ɵ�, ���ɲ��Ա����������嵥 | �� |
| P0-004-002-001-001-001 | ��дEntity�� | 2026-06-03T19:10 | ? | SysUser/SysRole/SysMenuʵ����: @TableNameӳ��/@TableId(ASSIGN_ID)/@TableLogic�߼�ɾ��/BaseEntity�̳�/passwordHash�ֶ�@TableField(select=false) | �� |
| P0-004-002-001-001-002 | ��дDTOVO�� | 2026-06-03T19:25 | ? | SysUserDTO/SysUserVO/SysRoleDTO/SysRoleVO/SysMenuDTO/SysMenuVO: CreateDTO��@NotBlank/@NotNullУ��, UpdateDTO��@NotNull id, QueryDTO����ҳ����, ListVO�ų������ֶ�, DetailVO�������ֶ�, @JsonFormat���ڸ�ʽ��, xxxName�ֵ䷭���ֶ� | �� |
| P0-004-002-001-001-003 | ��֤ʵ���� | 2026-06-03T19:45 | ? | ��֤SysUser/SysRole/SysMenuʵ����DDLһ����/ע��������/DTOУ��/VO��ʽ��������ͨ��������C1/C2/M1/m1/m2��5�����Ⲣ�ƶ��޸����� | �� |
| P0-004-002-002-001-001 | ��дSQL��� | 2026-06-03T19:50 | ? | UserMapper�ӿ�(5������)+UserMapper.xml(5���Զ���SQL:��ҳ��ѯ/�û���ΨһУ��/������ʷ/�����Ų��û�/��ɫ���Ʋ�ѯ), SysUserVO.ListVO����employeeName�ֶ�, ȫ��#{param}��������ע�� | �� |
| P0-004-002-002-001-002 | ��֤��дSQL | 2026-06-03T20:00 | ? | UserMapper SQL��֤���: Interface/XMLһ����?, ResultMapӳ��?, SQL�﷨?, ����?; ����2������(�������DDL��һ��CRITICAL/sys_user_password_history��ȱʧWARNING)����¼�޸����� | �� |
| P0-004-002-003-001-001 | ��д�ӿڶ���Service�ӿ� | 2026-06-03T20:10 | ? | UserService�ӿ�: �̳�IServiceX<SysUser>, ����assignRoles/resetPassword/updateStatus/unlockUser/getRoleNames/isUsernameUnique��6��ҵ�񷽷�ǩ�� | a6cfc780 |
| P0-004-002-003-001-002 | ��дServiceImplʵ���� | 2026-06-03T19:54 | ? | UserServiceImpl: �̳�ServiceImplX, ʵ��ȫ��6��ҵ�񷽷�(Sa-Token�߳�/BCrypt�������+��ʷ��/��ɫ��������/״̬��תУ��/Ψһ��У��/@Transactional�������) | �� |
| P0-004-002-003-001-003 | ��֤Service | 2026-06-03T20:15 | ? | ��֤UserService/UserServiceImpl: �ӿ�6������ǩ������/Impl�߼���ȷ/BCrypt����+������ʷ/Sa-Token����/@Transactionalλ����ȷ/BusinessException�쳣�淶/mvn compileͨ�� | �� |
| P0-004-002-004-001-001 | ��д�ӿڶ���Service�ӿ� | 2026-06-03T20:20 | ? | UserRoleService�ӿ�: �̳�IServiceX<SysUser>, ����assignRoles/removeUserRole/getUserRoleIds/getUserIdsByRoleId/hasRole��5��ҵ�񷽷�ǩ�� | 7b8a80b2 |
| P0-004-002-004-001-002 | ��дServiceImplʵ���� | 2026-06-03T20:25 | ? | UserRoleServiceImpl: �̳�ServiceImplX, ʵ��5������(Sa-Token kickout/@Transactional/BusinessException), UserMapper����4������+XMLӳ�� | 7325bd3e |
| P0-004-002-004-001-003 | ��֤Service | 2026-06-03T20:30 | ? | ��֤UserRoleService/UserRoleServiceImpl: �ӿ�5������ǩ������/Impl�߼���ȷ/StpUtil.kickout+SaaS-Token/@Transactionalλ����ȷ/BusinessException�쳣�淶/mvn compileͨ��/XML SQLӳ������ | �� |
| P0-004-002-005-001-001 | ��д�ӿڶ���Service�ӿ� | 2026-06-03T20:35 | ? | UserDeptService�ӿ�: �̳�IServiceX<SysUser>, ����assignDepts/removeUserDept/getUserDeptIds/getUserIdsByDeptId/hasDept/setPrimaryDept��6��ҵ�񷽷�ǩ�� | �� |
| P0-004-002-005-001-002 | ��дServiceImplʵ���� | 2026-06-03T20:40 | ? | UserDeptServiceImpl: �̳�ServiceImplX, ʵ��6������(Sa-Token kickout/@Transactional/BusinessException), UserMapper����7��dept����+XMLӳ�� | �� |
| P0-004-002-005-001-003 | ��֤Service | 2026-06-03T20:45 | ? | ��֤UserDeptService/UserDeptServiceImpl: �ӿ�6������ǩ������/Impl�߼���ȷ/StpUtil.kickout/@Transactionalλ����ȷ/BusinessException�쳣�淶/mvn compileͨ�� | �� |
| P0-004-002-006-001-001 | ��д�ӿڶ���Service�ӿ� | 2026-06-03T20:50 | ? | UserGroupService�ӿ�: �̳�IServiceX<SysUserGroup>, ����addMembers/removeMember/removeAllMembers/getMemberUserIds/getGroupIdsByUserId/hasMember/updateStatus��7��ҵ�񷽷�ǩ��; ����SysUserGroup/SysUserGroupMemberʵ��+UserGroupMapper | �� |
| P0-004-002-006-001-002 | ��дServiceImplʵ���� | 2026-06-03T20:55 | ? | UserGroupServiceImpl: �̳�ServiceImplX, ʵ��7������(entity��֤/BusinessException/@Transactional), UserGroupMapper����groupMembers���� | b81b0f95 |
| P0-004-002-006-001-003 | ��֤Service | 2026-06-03T20:51 | ? | ��֤UserGroupService: ���ձ�׼5��ȫͨ��, ����ɹ�; ���ֲ��޸�ȱʧUserGroupMapper.xml(6��SQLӳ��)
| P0-004-002-007-001-001 | ��д�ӿڶ���Service�ӿ� | 2026-06-03T21:00 | ? | UserService�ӿ�: ����changePassword(Long userId, String oldPassword, String newPassword)����ǩ��; UserServiceImpl����ռλʵ��ȷ������ͨ�� | �� |
| P0-004-002-007-001-002 | ��дServiceImplʵ���� | 2026-06-03T21:05 | ? | UserServiceImpl.changePassword: ����У��/Bcrypt��������֤/������ʷ������(���3��)/BCrypt����/insertPasswordHistory/StpUtil.kickout�߳��Ự/@Transactional���� | 4e941bdd |
| P0-004-002-007-001-003 | ��֤Service | 2026-06-03T21:10 | ? | ��֤UserService�����޸�/����Service: �ӿ�changePassword+resetPassword����ǩ������/BCrypt����+������ʷ������/Sa-Token kickout/@Transactionalλ����ȷ/BusinessException�淶/mvn compileͨ�� | 734edc2d |
| P0-004-002-008-001-001 | ��дController�� | 2026-06-03T21:16 | ? | UserController: @RestController+12��RESTful�ӿ�(CRUD/��ҳ/��ɫ/����/״̬/����/�û������)/@RequirePermission/RT+PageResult��Ӧ/Swaggerע��/����3��DTO���� | 5ad9f133 |
| P0-004-002-008-001-002 | ��д�ӿڷ��� | 2026-06-03T21:27 | ? | UserController�ӿڷ�������: ·��/api/system/user, @SaCheckPermission�滻, ��ҳkeyword/status/deptId, ���麬��ɫ����, BCrypt�������, ��ɾ��+��������, �������뷵��������, PUT /password���޸�, POST/DELETE /role������ɫ, UserService����resetPasswordAndReturn/deleteUserWithCleanup | 02e35e51 |
| P0-004-002-008-001-003 | ��֤Controller | 2026-06-03T21:33 | ? | ������֤ͨ��/RESTful URL�淶/RT+PageResult��Ӧ/@ValidУ��/@SaCheckPermission 14�˵�ȫ����/@Tag/@Operation���� | 889b8936 |
| P0-004-002-009-001-001 | ��д����̨�ۺ�SQL | 2026-06-03T21:40 | ? | UserWorkbenchVO�ۺ�����VO/UserMapper����3���ۺϲ�ѯ����/UserMapper.xml����3������̨�ۺ�SQL(�û�����+������+��������+��ɫ�ֲ�+���ŷֲ�)/UserService����getWorkbenchData | �� |
| P0-004-002-009-001-002 | ��֤���� | 2026-06-03T21:47 | ? | ����̨�ۺ�SQL��֤: SQL�﷨��ȷ/3���ۺϲ�ѯ�߼�����/LEFT JOIN��ֵ������ȷ/VO�ֶ�ӳ������/null��ȫ����/����ͨ��/����1������(Controllerȱ�ٹ���̨�˵�)�Ѽ�¼issues�ĵ� | �� |
| P0-004-003-001-001-001 | ��д�ӿڶ���Service�ӿ� | 2026-06-03T22:00 | ? | SysRoleService�ӿ�+SysRoleServiceImplʵ��/��ɫ����Ψһ��У��/״̬������/ɾ��ʱ����sys_user_role+sys_role_menu+sys_role_data+sys_role_field��������/SysRoleMapper��4��@Delete�������� | �� |
| P0-004-003-001-001-002 | ��дServiceImplʵ���� | 2026-06-03T22:10 | ? | SysRoleServiceImpl��ǿ: StpUtil.kickout�����û��߳�/״̬��תУ��(��ֹ�ظ�����)/@Transactional�������/BusinessException�쳣�淶/UserMapperע���ѯ��ɫ�����û� | �� |
| P0-004-003-001-001-003 | ��֤Service | 2026-06-03T22:20 | ? | ��֤SysRoleService/SysRoleServiceImpl: �ӿ�3��ҵ�񷽷�+IServiceX CRUD����/Ψһ��У��+״̬��ת+���������걸/Sa-Token kickout����/@Transactional������/BusinessException�淶/mvn compileͨ�� | �� |
| P0-004-003-002-001-001 | ��д�ӿڶ���Service�ӿ� | 2026-06-03T22:40 | ? | SysRoleMenuService�ӿ�: �̳�IServiceX<SysRole>, ����6��ҵ�񷽷�(assignMenus/assignMenusWithPermissions/removeRoleMenus/getRoleMenuIds/hasMenuPermission/copyMenus) | 87abc3a7 |
| P0-004-003-002-001-002 | ��дServiceImplʵ���� | 2026-06-03T22:50 | ? | SysRoleMenuServiceImpl: �̳�ServiceImplX, ʵ��6������(Sa-Token kickout/��ɫ������У��/�ռ�������/�˵�����д��/��ɫ����/@Transactional) | eb29b347 |
| P0-004-003-002-001-003 | ��֤Service | 2026-06-03T23:00 | ? | ��֤SysRoleMenuService/SysRoleMenuServiceImpl: �ӿ�6������ǩ������/Impl�߼���ȷ/Sa-Token kickout/@Transactionalλ����ȷ/BusinessException�淶/mvn compileͨ�� | �� |
| P0-004-003-003-001-001 | ��д�ӿڶ���Service�ӿ� | 2026-06-03T23:15 | ? | SysRoleDataScopeService�ӿ�: �̳�IServiceX<SysRoleDataScope>, 4��ҵ�񷽷�(getByRoleId/saveRoleDataScopes/deleteByRoleId/getScopeType), ����ȱʧǰ������SysRoleDataScopeʵ�� | c01e8c5e |
| P0-004-003-003-001-002 | ��дServiceImplʵ���� | 2026-06-03T23:25 | ? | SysRoleDataScopeServiceImpl: �̳�ServiceImplX, ʵ��4������(Sa-Token kickout/BusinessException/@Transactional/��ֵУ��), �½�SysRoleDataScopeMapper | e5dbdfa8 |
| P0-004-003-003-001-003 | ��֤Service | 2026-06-03T22:48 | ? | SysRoleDataScopeServiceTest: 13����Ԫ����ȫ��ͨ��, ����getByRoleId/saveRoleDataScopes/deleteByRoleId/getScopeType�ĸ����� | �� |
| P0-004-003-004-001-001 | ��д�ӿڶ���Service�ӿ� | 2026-06-03T20:30 | ? | ����SysRoleFieldPermissionService�ӿ�(extends IServiceX), ����getByRoleId/getByRoleIdAndTable/saveRoleFieldPermissions/deleteByRoleId/getPermissionType����; ͬʱ����SysRoleFieldPermissionʵ��ӳ��sys_role_field_permission�� | �� |
| P0-004-003-004-001-002 | ��дServiceImplʵ���� | 2026-06-03T23:35 | ? | SysRoleFieldPermissionServiceImpl: �̳�ServiceImplX, 5������(Sa-Token kickout/BusinessException/@Transactional), �½�SysRoleFieldPermissionMapper | 100cf765 |
| P0-004-003-004-001-003 | ��֤Service | 2026-06-03T20:36 | ? | ��֤SysRoleFieldPermissionService/SysRoleFieldPermissionServiceImpl: �ӿ�5������ǩ������/Impl�߼���ȷ/StpUtil.kickout/@Transactionalλ����ȷ/BusinessException�淶/mvn compileͨ�� | �� |
| P0-004-003-005-001-001 | ��д�ӿڶ���Service�ӿ� | 2026-06-03T20:40 | ? | ����SysButtonPermissionService�ӿ�(extends IServiceX\<SysMenu\>), ����checkPermission/getUserPermissions/getRolePermissions/getButtonsByMenuId/getButtonsByUserId/refreshCache����; mvn compileͨ�� | �� |
| P0-004-003-005-001-002 | ��дServiceImplʵ���� | 2026-06-03T20:45 | ? | ʵ��SysButtonPermissionServiceImpl(extends ServiceImplX\<SysMenuMapper, SysMenu\>), ����StpUtil.hasPermission/kickout, ���з���null��������, ����SysMenuMapper(��selectPermissionCodesByRoleId/UserId/selectButtonsByParentId/UserId/selectByPermissionCodeע��SQL); mvn compileͨ�� | �� |
| P0-004-003-005-001-003 | ��֤Service | 2026-06-03T20:50 | ? | ��֤SysButtonPermissionService/SysButtonPermissionServiceImpl: �ӿ�6��������/Impl Sa-Token������ȷ/������������@Transactional/null�������Ž���/mvn compile BUILD SUCCESS | �� |
| P0-004-003-006-001-002 | ��д�ӿڷ��� | 2026-06-03T23:44 | ? | SysRoleController: ����POST /menu�˵�������+GET /menu/{roleId}�˵���+POST /inheritance�̳�(4���˵�)+POST /exclusion����(4���˵�)+POST /data-scope����Ȩ������; �½�SysRoleInheritance/SysRoleExclusionʵ��+Mapper+Service(Impl); �½�SysDataPermissionSchemeController(CRUD)+SysFieldPermissionSchemeController(CRUD)��DDL+Entity+Mapper+Service; �½�MenuPermissionBatchDTO; SysMenu����children�ֶ�; mvn clean compile BUILD SUCCESS | dd281ba8 |
| P0-004-003-007-001-001 | ��дDDLEntityMapperServiceController | 2026-06-03T23:28 | ? | Flyway DDL: sys_role_inheritance(��ɫ�̳б�)����+����+�ع�, sys_role_exclusion(��ɫ�����)����+����+�ع�, ��4��SQL�ļ�, mvn compileͨ�� | e1f36605 |
| P0-004-003-006-001-001 | ��дController�� | 2026-06-03T23:45 | ? | SysRoleController: @RestController+@RequestMapping("/api/system/role"), ע��5��Service, 26��RESTful�ӿ�(��ɫCRUD+�˵�Ȩ��+����Ȩ��+�ֶ�Ȩ��+��ťȨ��), @SaCheckPermissionȫ����, RT+PageResult��Ӧ, @Operationע������ | �� |
| P0-004-003-008-001-001 | ��дDDLEntityMapperServiceController | 2026-06-03T23:55 | ? | ����Ȩ�޷�������: Flyway DDL(sys_data_permission_scheme+sys_data_permission_scheme_role)+�ع��ű�, SysDataPermissionSchemeʵ��/Mapper/Service/ServiceImpl/Controller����CRUD, mvn compileͨ�� | �� |
| P0-004-003-009-001-001 | ��дDDLEntityMapperServiceController | 2026-06-04T00:05 | ? | �ֶ�Ȩ�޷�������: Flyway DDL(sys_field_permission_scheme+role+detail)+�ع��ű�(����), SysFieldPermissionScheme+Detailʵ��/Mapper/Service/ServiceImpl/Controller����CRUD���Ѿ���, mvn compileͨ�� | cb6e7d0f |
| P0-004-003-006-001-003 | ��֤Controller | 2026-06-04T00:20 | ? | ��֤���޸�Ȩ������Controller: SysRoleController/SysDataPermissionSchemeController/SysFieldPermissionSchemeControllerȫ��ͨ��5�����ձ�׼, ������@Valid+BindingResult����У��, mvn compileͨ�� | �� |
| P0-004-004-001-001-001 | ��д�ӿڶ���Service�ӿ� | 2026-06-04T00:30 | ? | SysMenuService�ӿ�: �̳�IServiceX<SysMenu>, ����getMenuTree/getMenuTreeByUserId/isPermissionCodeUnique/updateStatus/deleteMenuWithChildren��5��ҵ�񷽷�ǩ�� | �� |
| P0-004-004-001-001-002 | ��дServiceImplʵ���� | 2026-06-04T00:15 | ? | SysMenuServiceImpl: �̳�ServiceImplX, ʵ��5������(�˵�������/���û�Ȩ�޹�����/Ȩ����Ψһ��У��/״̬������/����ɾ��), @Transactional�������, BusinessException�쳣�淶 | �� |
| P0-004-004-001-001-003 | ��֤Service | 2026-06-04T00:05 | ? | ����ͨ��, �������5�����ձ�׼ȫ��ͨ��(�ӿ�ǩ������/ҵ���߼���ȷ/У���걸/����ע��λ����ȷ/�쳣�淶) | �� |
| P0-004-004-002-001-001 | ��д�ӿڶ���Service�ӿ� | 2026-06-04T00:45 | ? | SysMenuMobileService�ӿ�: �̳�IServiceX\<SysMenu\>, ����getMobileMenuTree/getMobileMenuTreeByUserId��2���ƶ��˲˵�ҵ�񷽷�ǩ�� | �� |
| P0-004-004-002-001-002 | ��дServiceImplʵ���� | 2026-06-04T01:00 | ? | SysMenuMobileServiceImpl: �̳�ServiceImplX, ʵ��getMobileMenuTree/getMobileMenuTreeByUserId, �ƶ����ų�button���Ͳ˵�, Ȩ�������, ���νṹ����, �շ�֧���� | �� |
| P0-004-004-002-001-003 | ��֤Service | 2026-06-04T00:32 | ? | ��֤SysMenuMobileService/SysMenuMobileServiceImpl: �ӿ�2������ǩ������/Impl�ƶ��˲˵��������߼���ȷ/Ȩ�޹���/nullsafe/mvn compileͨ�� | �� |
| P0-004-004-003-001-001 | ��д�ӿڶ���Service�ӿ� | 2026-06-04T00:40 | ? | ����SysMenuTreeService�ӿ�: 4������ǩ��( getMenuTree/getMenuTreeByUserId/buildTree/filterEmptyBranches)/�̳�IServiceX<SysMenu>/mvn compileͨ�� | �� |
| P0-004-004-003-001-002 | ��дServiceImplʵ���� | 2026-06-04T01:10 | ? | SysMenuTreeServiceImpl: �̳�ServiceImplX, ʵ��4������(getMenuTreeȫ����/getMenuTreeByUserId��Ȩ�޹���/buildTree�ݹ齨��/filterEmptyBranches���˿շ�֧), Ȩ����SQL����, nullsafe | 9614b53b |
| P0-004-004-003-001-003 | ��֤Service | 2026-06-04T01:15 | ? | ��֤SysMenuTreeService/SysMenuTreeServiceImpl: �ӿ�4������ǩ������/Implҵ���߼���ȷ(�˵�������+���û�Ȩ�޹���+�շ�֧����)/nullsafe/������������@Transactional/mvn compileͨ�� | e7dae09e |
| P0-004-004-004-001-001 | ��дController�� | 2026-06-04T08:10 | ? | SysMenuController: @RestController+@RequestMapping("/api/system/menus"), ע��3��MenuService, 14��RESTful�ӿ�(�˵�CRUD+�˵���+�ƶ��˲˵�), @SaCheckPermissionȫ����, RT+PageResult��Ӧ, @Operationע������, mvn compile BUILD SUCCESS | 120c0dc0 |
| P0-004-004-004-001-002 | ��д�ӿڷ��� | 2026-06-04T09:00 | ? | SysMenuController����6������: GET /tree/current(��ǰ�û��˵���/StpUtil.getLoginIdAsLong), GET /mobile-tree/current(��ǰ�û��ƶ��˲˵���), POST /mobile, PUT /mobile/{id}, DELETE /mobile/{id}(�ƶ��˲˵�CRUD), mvn compile BUILD SUCCESS | 1802604a |
| P0-004-004-004-001-003 | ��֤Controller | 2026-06-04T10:10 | ? | ��֤SysMenuController����: 15��RESTful�˵�URL����/api/{module}/{resource}�淶, RT<T>/PageResult<T>��Ӧ��ȷ, @Valid+BindingResultУ������, @SaCheckPermissionȫ����, @Operationע������, mvn compileͨ��������㾯�� | ee8eb09a |
| P0-004-005-001-001-001 | ��д�ӿڶ���Service�ӿ� | 2026-06-04T11:00 | ? | AuthMethodService�ӿ�: �̳�IServiceX<AuthMethod>, ����listEnabled/isMethodNameUnique/isMethodTypeUnique/updatePriority/enable/disable��6��ҵ�񷽷�; ����AuthMethodʵ��ӳ��auth_method�� | �� |
| P0-004-005-001-001-002 | ��дServiceImplʵ���� | 2026-06-04T11:20 | ? | AuthMethodServiceImpl: �̳�ServiceImplX, ʵ��6������(������+���ȼ�/Ψһ��У��/BusinessException/@Transactional); ����AuthMethodMapper | �� |
| P0-004-005-001-001-003 | ��֤Service | 2026-06-04T17:00 | ? | ��֤AuthMethodService/AuthMethodServiceImpl: �ӿ�6������ǩ������/Implҵ���߼���ȷ/Ψһ��У��+״̬��ת�걸/@Transactionalλ����ȷ/BusinessException�淶/mvn compileͨ�� | �� |
| P0-004-005-002-001-001 | ��д�ӿڶ���Service�ӿ� | 2026-06-04 | ? | AuthPasswordPolicyService�ӿ�: �̳�IServiceX\<AuthPasswordPolicy\>, ����getCurrentPolicy/isPolicyNameUnique/enable/disable/validatePassword��5��ҵ�񷽷�; ����AuthPasswordPolicyʵ��+AuthPasswordPolicyMapper | �� |
| P0-004-005-002-001-002 | ��дServiceImplʵ���� | 2026-06-04 | ? | AuthPasswordPolicyServiceImpl: �̳�ServiceImplX, ʵ��5������(getCurrentPolicy��ǰ���ò���/isPolicyNameUniqueΨһУ��/enable�Ƚ�������������/disable״̬У��/validatePassword����ǿ��У��), @Transactional��enable/disable, BusinessException�淶, mvn compileͨ�� | �� |
| P0-004-005-002-001-003 | ��֤Service | 2026-06-04 | ? | ��֤AuthPasswordPolicyService: �ӿ�5������ǩ������, ServiceImplʵ����ȷ, Ψһ��У��/״̬��ת�걸, @Transactionalλ����ȷ, BusinessException�淶, mvn compileͨ�� | �� |
| P0-004-005-003-001-001 | ��дController�� | 2026-06-04 | ? | AuthConfigController: @RestController+@RequestMapping, ע��AuthMethodService/AuthPasswordPolicyService, 18��RESTful�˵�(��֤��ʽ9+�������9), @RequirePermissionȨ�޿���, RT<T>/PageResult<T>ͳһ��Ӧ, Swagger @Operationע������ | �� |
| P0-004-005-003-001-002 | ��д�ӿڷ��� | 2026-06-04T11:45 | ? | AuthConfigController���������豸����(��ҳ��ѯ+ǿ������StpUtil.logoutByTokenValue)�͹���̨�ۺ�(�����豸��/���յ�¼�ɹ�/ʧ�ܴ���/SSO������)�˵�, mvn compileͨ�� | �� |
| P0-004-005-003-001-003 | ��֤Controller | 2026-06-04T12:30 | ? | ��֤AuthConfigController: 5�����ձ�׼ȫ��ͨ��, ����@Valid+BindingResult����У��, mvn compileͨ�� | �� |
| P0-004-005-004-001-001 | ��дOnlineDeviceService | 2026-06-04 | ? | OnlineDeviceService�ӿ�(�̳�IServiceX, ����kickDevice/countOnline����)+OnlineDeviceServiceImpl(�̳�ServiceImplX, ʵ��StpUtil.logoutByTokenValueǿ������+�����豸����+@Transactional+BusinessException), AuthConfigController�ع�ע��OnlineDeviceService�滻ֱ��Mapper���� | �� |
| P0-004-005-004-001-002 | ��дServiceImplʵ���� | 2026-06-04T12:00 | ? | ��֤OnlineDeviceServiceImpl����(�̳�ServiceImplX,ʵ��kickDevice/countOnline,ʹ��StpUtil+@Transactional+log.info),����ͨ�� | 74adc2dc |
| P0-004-005-004-001-003 | ��֤Service | 2026-06-04T14:05 | ? | ��֤OnlineDeviceService�ӿ�(IServiceX+kickDevice+countOnline)+OnlineDeviceServiceImpl(StpUtil.logoutByTokenValue+״̬��תonline��kicked+@Transactional+log)/Controller����(pageList+kickDevice+countOnline)/mvn compileͨ��/5�����ձ�׼ȫ��ͨ�� | �� |
| P0-004-005-005-001-001 | ��д����̨�ۺ�SQL | 2026-06-04T12:00 | ? | AuthConfigWorkbenchVO/AuthConfigWorkbenchMapper/XML: ����̨�ۺ�SQL(��֤��ʽ+�������+�����豸+��¼ͳ��+��¼��ʽ�ֲ�+ÿ��ͳ��), ȫ��#{param}������, AuthConfigController�ع�ʹ����Mapper����VO, mvn compileͨ�� | �� |
| P0-004-005-005-001-002 | ��֤���� | 2026-06-04T14:20 | ? | ��֤����̨�ۺ�SQL: ����ͨ��/60�����ȫ��ͨ��/SQL���������/���⻧����/ʱ�䷶Χ/�߽�����, ���Ա���+�����嵥�ѹ鵵 | �� |
| P0-004-006-001-001-001 | ��дҳ����� | 2026-06-04T14:20 | ? | ��¼ҳ�����: Vue3+TS+ElementPlus������¼ҳ, ����֤��/��ס��/����У��/���ʻ�/API��/Composable, ����ͨ�� | 7290fed7 |
| P0-004-006-002-001-001 | ��д���Ĵ��� | 2026-06-04T14:35 | ? | ��¼�����߼�: useLogin��ǿ(������ӳ��+��֤��ˢ��), ��̬·������(menuTree��addRoute), Token��ͳһ(satoken+refresh_token), API�˵�����, ��������, pnpm buildͨ�� | e340d79b |
| P0-004-006-001-001-002 | ��д���ݰ��뽻���߼� | 2026-06-04T14:47 | ? | ����У���������(�û���3-20λ/����8-32λ/��֤��4λ), ��ס��localStorage���ܴ洢(btoa+encodeURIComponent), loadRememberedUsernameҳ������Զ����, vue-tsc����ͨ�� | 9b50bb58 |
| P0-004-006-001-001-003 | ��֤ǰ��ҳ�濪��ҳ�� | 2026-06-04T14:55 | ? | ��¼ҳȫ����֤:60��ܼ��ͨ��/12���˲���ͨ��/����6������(2��Criticalȱʧ�˵�+1��High������ƥ��+1��Medium���Ͳ�����+2��Low������©), ���Ա���+�����嵥�ѹ鵵 | �� |
| P0-004-006-002-001-002 | ��֤���� | 2026-06-04T15:00 | ? | ��¼�����߼���֤:43��������ͨ��/����4������(1���·���:ǰ�����Ӧ��code=200 vs code=0��ƥ�� + 3����֪API�˵�����), ���Ա���+�����嵥�ѹ鵵 | �� |
| P0-004-006-003-001-001 | ��д���Ĵ��� | 2026-06-04T15:15 | ? | �˳���¼�����߼�: logoutApi�ݴ�����, ���userStore/permissionStore״̬, ���localStorage, ��ת/loginЯ��redirect����, pnpm buildͨ�� | �� |
| P0-004-006-003-001-002 | ��֤���� | 2026-06-04T15:20 | ? | �˳���¼�߼���֤: 34���˲���ȫ��ͨ��, AuthControllerTest$Logout 2��, AuthServiceTest$Logout 4��(����/����/����/�ݴ�), pnpm buildͨ��, ���Ա���+�����嵥�ѹ鵵 | �� |
741	| P0-004-006-004-001-001 | ��д���Ĵ��� | 2026-06-04T16:00 | ? | Tokenˢ�º����߼�: �޸�API�˵�(/api/auth/token/refresh), ͳһlocalStorage key(TOKEN_KEY/REFRESH_TOKEN_KEY), ��������ѭ������(isRefreshRequest���), �ǳ��ض���Я��redirect����, cancelRequest�������˵�ͬ���޸�, pnpm buildͨ�� | �� |
| P0-004-006-004-001-002 | ��֤���� | 2026-06-04T16:30 | ? | ��дVitest��Ԫ����(9������), ��װjsdom, ����vitest.config.ts; ��֤ͨ��: ������/������/��ѭ��/����/�߽�; �������Ա���������嵥; pnpm buildͨ�� | �� |
| P0-004-007-001-001-001 | ��д���Ĵ��� | 2026-06-04T08:54 | ? | ·������NProgress����: beforeEach����NProgress.start(), ����afterEach��������ҳ�����+NProgress.done(), ������/Token/��̬·��/Ȩ��У���߼�����, pnpm buildͨ�� | �� |
| P0-004-007-001-001-002 | ��֤���� | 2026-06-04T12:10 | ? | ��֤·�����������߼�: 15��ܼ��+6��߽�����+3���쳣����ȫ��ͨ��, vue-tsc�����޴���, ���Ա���+�����嵥�ѹ鵵 | �� |
| P0-004-007-002-001-001 | ��д���Ĵ��� | 2026-06-04T12:10 | ? | usePermission composable(hasPermission/hasAnyPermission/hasRole/superadmin bypass) + v-roleָ�� + 12��vitest��Ԫ����ȫ��ͨ�� | �� |
| P0-004-007-002-001-002 | ��֤���� | 2026-06-04T12:15 | ? | ��֤usePermission composable: 12��vitestȫ��ͨ��/pnpm buildͨ��/�޸�vi��������/���Ա���+�����嵥�ѹ鵵 | (pending) |
| P0-004-007-003-001-001 | ��д���Ĵ��� | 2026-06-04T12:20 | ? | ������˵���̬��Ⱦ���Ĵ���: Sidebar/index.vue(permissionStore�˵���+el-menu�ݹ���Ⱦ+�۵�+·�ɸ���)/SidebarItem.vue(�ݹ�������������ֲ˵�����+visible����+����)/MenuItemIcon.vue(Element Plus/SVG/�Զ���ͼ��), pnpm buildͨ�� | cf1a5af6 |
| P0-004-007-003-001-002 | ��֤���� | 2026-06-04T17:30 | ? | ������˵���̬��Ⱦ��֤: ����28��vitest����(routeToMenuItem 8/permission utils 13/menuPipeline 18), ȫ��67��ͨ��(������21��), �������7�����ͨ��, ���Ա���+�����嵥�ѹ鵵 | (pending) |
| P0-004-007-004-001-001 | ��дҳ����� | 2026-06-04 | ? | ϵͳ��������ҳ��: Vue3+TS+ElementPlus����CRUDҳ��(views/system/params/index.vue), ��չapi/modules/system.ts(7��sys param API����), ����useSystemParam composable(����/CRUD/����ˢ��), vue-tsc+vite buildͨ�� | c6ebd29d |
| P0-004-007-004-001-002 | ��д���ݰ��뽻���߼� | 2026-06-04 | ? | ��չapp store����ϵͳ����״̬(systemName/logoUrl/defaultPageSize/dateFormat/themeColor/watermarkEnabled)��initAppConfig�첽��ʼ��, ����App.vueΪrouter-view+onMounted��ʼ��, ����useAppInit composable(��ʼ��/����ɫ/ˮӡ��Ⱦ), vue-tscͨ�� | �� |
| P0-004-007-004-001-003 | ��֤ǰ��ҳ�濪��ҳ�� | 2026-06-04 | ? | ��֤ϵͳ����ǰ��ҳ��: pnpm build����2�����ʹ���(valueTypeTag����ֵ/DefaultRow����), useAppInitδ����, ����CRUD/����ˢ��/����У�鹦��ʵ����ȷ, ���Ա���+�����嵥�ѹ鵵 | �� |
| P0-004-008-000-001-001 | ��д���Ĵ��� | 2026-06-04T12:10 | ? | �û���������̨���Ĵ���: Vueҳ��(4��KPI��Ƭ+����/ECharts����ͼ+��ͼ/��ݲ���/��¼��־����), API��(workbench.ts), ���Ͷ���(workbench.ts), useWorkbench composable(ECharts�������ڹ���/��Ӧʽ����), pnpm buildͨ��(workbench��������) | (pending) |
| P0-004-008-000-001-002 | ��֤���� | 2026-06-04T13:05 | ? | ��֤���޸��û���������̨: ����5������(Controllerȱ�˵�/VO�ֶβ�ƥ��/·��δע��/ȱloginTrend+recentLogins��ѯ/ͼ�����ؿհ�), ȫ���޸�, mvn compile+pnpm buildͨ��, ���Ա��������� | (pending) |
| P0-004-008-001-001-001 | ��д���Ĵ��� | 2026-06-04T13:30 | ? | �û������б�ҳ���Ĵ���: UserList.vue(����/ɸѡ/����/��ҳ/����), API��(user.ts-8���˵㺯��), ���Ͷ���(UserListItem/UserPageQuery), ��������(debounce 300ms), v-permissionȨ�޿���, pnpm buildͨ�� | 5c8d159b |
| P0-004-008-001-001-002 | ��֤���� | 2026-06-04T13:40 | ? | �û������б�ҳ��֤: ��̬���+������֤+API��Լ���+���ܺ˶�+Ȩ�����; ����7������(��1��critical-RT��Ӧ��200vs0��ƥ��); ���Ա����������嵥������ | 33c63dd7 |
| P0-004-008-002-001-001 | ��д���Ĵ��� | 2026-06-04T13:50 | ? | �û��༭�������Ĵ���: UserForm.vue(����/�༭Dialog/����У��/������/��ɫ����/ͷ���ϴ�)/role.ts API/index.vue����UserForm���/pnpm buildͨ�� | a0f3f3e0 |
| P0-004-008-003-001-001 | ��д���Ĵ��� | 2026-06-04T14:40 | ? | UserRoleDialog.vue��ɫ���䵯��: el-dialog+checkbox-group+��ɫ����У��+���ܱ���+���ժҪ+����ȷ��/API����assignUserRoles+getRoleExclusions+checkRoleExclusion | �� |
| P0-004-008-003-001-002 | ��֤���� | 2026-06-04T15:00 | ? | �û���ɫ���䵯����֤: ��̬�������26����(20ͨ��/2Bug/4����)+������֤����������+�߽�9��+�쳣5��; ��������: isCurrentUserSuperadmin������/��ɫ��ƥ�����; ���Ա����������嵥������ | �� |
| P0-004-008-004-001-001 | ��д���Ĵ��� | 2026-06-04T15:30 | ? | UserDeptDialog.vue���Ź�������: el-dialog(600px)+el-tree��ѡ���ѡ(��ѡ����)+������radio+����ȷ��+���ȫ��/API����assignUserDepts+clearUserDepts+getDeptTree | 40f3b15a |
| P0-004-008-004-001-002 | ��֤���� | 2026-06-04T16:00 | ? | �û����Ź���������֤: ǰ���ȫ��·�������(8���ļ�)/����4������(2CRITICAL+2HIGH): 3��API�˵�ȱʧ+DetailVOȱdeptIds�ֶ�+primaryDeptId������·����+is_primaryӲ����; ���Ա����������嵥������ | �� |
| P0-004-008-005-001-001 | ��д���Ĵ��� | 2026-06-04T16:00 | ? | �û�������б�ҳ: UserGroupController(CRUD+��ҳ��ѯ+����Ψһ��+��Ա����)/SysUserGroupVO/DTO/mapper.countMembersByGroupIds/UserGroupList.vue(����/��ҳ/״̬�л�/����ɾ��)/UserGroupForm.vue(�����༭)/API��+���Ͷ��� | �� |
| P0-004-008-005-001-002 | ��֤���� | 2026-06-04T16:15 | ? | �û�������б�ҳ��֤: �޸�4������(3��TS���ʹ���+1��ȱʧ��Ա������ť)/mvn compileͨ��/���ܹ��14��ȫ������ͨ��/���Ա����������嵥������ | �� |
| P0-004-008-006-001-001 | ��д���Ĵ��� | 2026-06-04T17:00 | ? | �û��������ǿ: UserGroupForm.vue(720px+el-transfer��Ա����+el-checkbox-group��ɫ����+����У��2-50/2-30)/SysUserGroupRoleʵ��+DDL/Controller����6����Ա/��ɫ�˵�/Service+Mapper��ɫ����/mvn compileͨ�� | ced185cc |
| P0-004-008-006-001-002 | ��֤���� | 2026-06-04T17:30 | ? | �û��������֤: ��̬�������40����(���Ĺ���23+�߽�11+�쳣����6)ȫ��ͨ��/mvn compile+pnpm buildͨ��/����3������������/���Ա����������嵥������ | 7988cafb |

| P0-004-009-000-001-001 | KPI��Ƭ������� | 2026-06-04T14:00 | ? | KpiCard�ɸ������+Ȩ�����ù���̨ҳ��(4��KPI��Ƭ+2��EChartsͼ��)+API��+composable+·��ע��, vite buildͨ�� | 8e790211 |

| P0-004-009-000-002-001 | EChartsͼ��������� | 2026-06-04T18:00 | ? | ��ǿuseAuthConfigWorkbench(����/��״/��ͼ/�״�4��ͼ��+����PNG)+����̨ҳ��(ʱ��ɸѡ/ά���л�/���عǼ���/������ť), ���ͼ��ͨ�� | 23833679 |

| P0-004-009-000-003-001 | ����̨ȫ�������� | 2026-06-04T18:30 | ? | ǰ��˱���ͨ��/API��Լ12�ֶ�ȫƥ��/�������10����֤ͨ��/����6������(I01-I06)���޸�����/���Ա���+�����嵥������ | �� |

| P0-004-009-001-001-001 | ��д���Ĵ��� | 2026-06-04T19:00 | ? | api/types/role.ts���Ͷ���+api/modules/role.ts API��(��ҳ/CRUD/״̬/����У��)+views/system/role/RoleList.vue�б�ҳ(����/ɸѡ/����/��ҳ/superadmin����) | 9e99587e |

| P0-004-009-001-001-002 | ��֤���� | 2026-06-04T20:30 | ? | ���Ա���+�����嵥����(38����37ͨ��)/�޸�2������(dataScope all��ǩɫ+roleCode��������)/��¼4������������(userCount��/dataScopeɸѡAPI/�����ֶ�Ȩ�ް�ť/custom��ɫ) | �� |

| P0-004-009-002-001-001 | ��д���Ĵ��� | 2026-06-04T21:30 | ? | RoleForm.vue����ҳ(��ɫCRUD/���ݷ�Χ����/��ɫ�̳�/��ɫ����/superadmin����)+api/modules/role.ts��չ(�̳�/����API����) | 6263a42d |
| P0-004-009-002-001-002 | ��֤���� | 2026-06-04T22:00 | ? | ���Ա���+�����嵥����(46����44ͨ��)/����2������(customDeptIdsδ�־û�/layoutƫ��dialog���) | f57ba67c |

| P0-004-009-003-001-001 | ��д�����ļ��� | 2026-06-04T23:00 | ? | MenuPermissionProperties������(menu-permissionǰ׺/�˵���չʾ+Ȩ�޷�������)+application.yml������ | 8eb01073 |
| P0-004-009-003-001-002 | ��֤��д���������� | 2026-06-04T23:15 | ? | additional-spring-configuration-metadata.json����Ԫ����(9����+5��ʾֵ)/application.yml������֤/����ͨ�� | �� |

| P0-004-009-004-001-001 | ��д�����ļ��� | 2026-06-04T23:30 | ? | DataPermissionProperties������(data-permissionǰ׺/���ݷ�Χչʾ+��������)+application.yml������ | �� |

| P0-004-009-004-001-002 | ��֤��д���������� | 2026-06-04T23:45 | ? | additional-spring-configuration-metadata.json(8����+3����+4��ʾö��ֵ)/����ͨ�� | �� |

| P0-004-009-005-001-001 | ��д�����ļ��� | 2026-06-04T23:50 | ? | FieldPermissionProperties������(field-permissionǰ׺/����չʾ+�ֶι�������)+application.yml������/����ͨ�� | b895f62d |

| P0-004-009-005-001-002 | ��֤��д���������� | 2026-06-04T23:55 | ? | additional-spring-configuration-metadata.json(6����+3����+3��ʾö��ֵ)/����ͨ�� | �� |

| P0-004-009-006-001-001 | ��дҳ����� | 2026-06-05T00:10 | ? | api/types/menu.ts��չ(SysMenuListItem/SysMenuCreateDTO��6������)+api/modules/menu.ts��չ(9��CRUD����)+views/system/menu/index.vue�˵�����ҳ(���α���/����ɸѡ/�����༭����/v-permissionȨ��/ȫ��չ���۵�), ǰ�˹���ͨ�� | �� |

| P0-004-009-006-001-002 | ��д���ݰ��뽻���߼� | 2026-06-05T01:00 | ? | composables/permission-ui/useIconSelector.ts(90+ͼ��ѡ�������ӳ��)+views/system/menu/index.vue��ǿ(ͼ��ѡ�񵯴�/����ק����sortOrder/Ȩ�ޱ����Զ���ʾ/ˢ�°�ť/�˵������ֶ�����), ǰ�˹���ͨ�� | 0c29688a |

| P0-004-009-006-001-003 | ��֤ǰ��ҳ�濪��ҳ�� | 2026-06-05T01:15 | ? | test-report.md(������֤/14�����/12������/6��������/5��ק��/8API��/4������/10�߽���ȫ��PASS)+issues.md(2��LOW��������/Ӳ��������/Ȩ��ע����©), ǰ��˱����ͨ�� | f4c72238 |

## P0-004-010 - ��֤����ǰ��ҳ��

| ������ | �������� | ���ʱ�� | ״̬ | ժҪ | SHA |
|---------|---------|---------|:---:|------|:---:|
| P0-004-010-000-001-001 | ��д���Ĵ��� | 2026-06-04T18:00 | ? | AuthWorkbench����̨(Vue+KpiCard+ECharts����/����ͼ+��ݲ���+�����¼��־)+API��+Composable+���Controller/Mapper/VO/SQL | 0f2172d9 |
| P0-004-010-000-001-002 | ��֤���� | 2026-06-04T20:20 | ? | test-report.md(29����֤/28ͨ��)+issues.md(1��CRITICALǰ���code��ƥ��/2��MINOR), ��˱���ͨ�� | ee7539c3 |
| P0-004-010-000-001-001 | ��д���Ĵ��� | 2026-06-05T02:00 | ? | ��֤���ù���̨ǰ�˺��Ĵ���: ����KPI��Ƭ(�����豸/���յ�¼�ɹ�/ʧ��/SSO����)+��¼��������ͼ+��֤��ʽ����ͼ+��ݲ���(�������/��֤��ʽ/SSO/�����豸)+�����¼��־����(10��/�ɹ���ɫ/ʧ�ܺ�ɫ)+60���Զ�ˢ��; �������RecentLoginVO+selectRecentLogins��ѯ; mvn compile+pnpm buildͨ�� | 0d5cbf34 |
| P0-004-010-001-001-001 | ��д�����ļ��� | 2026-06-05T01:30 | ? | AuthMethodProperties.java(@ConfigurationProperties prefix=auth-method/Page+Form+Priority����Ƕ������/@ValidatedУ��)+application.yml(auth-method��:��ҳĬ��ֵ/�����ֶγ���/���ȼ���Χ/Ĭ��ֵ), mvn compileͨ�� | �� |
| P0-004-010-002-001-001 | ��д�����ļ��� | 2026-06-05T01:20 | ? | PasswordPolicyProperties.java(@ConfigurationProperties prefix=password-policy/Page+Form+Password+Lockout+Expire+History����Ƕ������/@ValidatedУ��)+application.yml(password-policy��:��ҳ/���븴�Ӷȹ���/��������/���ڲ���/��ʷ��������), mvn compileͨ�� | c1c475f9 |
| P0-004-010-002-001-002 | ��֤��д���������� | 2026-06-04T20:45 | ? | additional-spring-configuration-metadata.json����password-policyԪ����(7����+19����)+application.yml��֤+application-dev.yml/prod.yml������֤+������Ϣ���+mvn compileͨ�� | �� |
| P0-004-010-003-001-001 | ��д���Ĵ��� | 2026-06-04T21:15 | ? | LoginLogList.vue��¼��־�б�ҳ(����/����/��ҳ/����CSV/��ճ���)+api/types/loginLog.ts+api/modules/loginLog.ts(3��API����)+·��ע��,vite buildͨ�� | 43f67a2b |
| P0-004-010-003-001-002 | ��֤���� | 2026-06-04T21:30 | ? | ���������֤ͨ��(26����92.3%ͨ����)+����test-report.md+issues.md(����6����:1����/1�е�/4��΢), ��¼��־�б�ҳǰ�˴����������� | �� |
| P0-004-010-004-001-001 | ��д���Ĵ��� | 2026-06-04T21:55 | ? | OnlineDeviceList.vue�����豸����ҳ(����/����/�豸����ͼ��/ǿ������/�����߳�/30���Զ�ˢ��)+API��(types+modules)+·��ע��, vite buildͨ�� | �� |
| P0-004-010-004-001-002 | ��֤���� | 2026-06-04T21:55 | ? | �����豸����ҳ��֤: 20��������ȶ�ͨ��/���ʹ����޸�(line 137 DefaultRow��OnlineDeviceItem)/���Ա���+�����嵥�鵵(3������:1�޸�/2����)/pnpm build�����豸�ļ������ʹ��� | �� |

## P0-004-011 - ��¼��ȫ��ǿ

| ������ | �������� | ���ʱ�� | ״̬ | ժҪ | SHA |
|---------|---------|---------|:---:|------|-----|
| P0-004-011-001-001-001 | ʵ����֤������ | 2026-06-04T22:25 | ? | ��֤CaptchaService��֤�����ɹ���: 11/11����ͨ��/������>80%/����4������(ȱͼƬ���ɽӿ�/Ӳ��������/��������֤���ı�/Math.random�ǰ�ȫ�����)/���Ա���+�����嵥�ѹ鵵 | f39f24f0 |
| P0-004-011-001-001-003 | ��֤ͼ����֤�� | 2026-06-04T21:40 | ? | ��֤ͼ����֤��ȫ����: 60/60����ͨ��(CaptchaService 11+AuthProperties 15+AuthService 34)/��������+У��+�߽�+�쳣+һ��������/���Ա���+�����嵥(4����)�ѹ鵵 | (pending) |
| P0-004-011-001-002-001 | ǰ�˵�¼ҳ��֤����� | 2026-06-04T21:35 | ? | ��֤ǰ�˵�¼ҳ��֤�����: 40/40���ͨ��/���Ǻ��Ĺ���+�߽�����+���ʻ�+����ܹ�+��˶Խ�/����3������(ȱAPI�˵�/��ͼƬ����/����δע��)/���Ա���+�����嵥�ѹ鵵 | 399ccd82 |
| P0-004-011-001-002-002 | ��˵�¼�ӿڸ��� | 2026-06-04T21:50 | ? | ����CaptchaVO/��չCaptchaService����ͼ����֤��ͼƬ����(BufferedImage+������+���+Base64)/����GET /api/auth/captcha�˵�/60��authģ�����ȫ��ͨ�� | f5bc94c3 |
| P0-004-011-002-001-001 | ʵ������ǿ��У�鹤���� | 2026-06-04T22:00 | ? | PasswordValidator(@Component)ʵ����ѡ�����Ӷ�/����8-32/����3λ��ͬ�ַ�/�û���ƥ��У��+PasswordStrengthö��(WEAK/MEDIUM/STRONG/VERY_STRONG)+PasswordValidationResult�����+54�Ԫ����ȫ��ͨ�� | (pending) |
| P0-004-011-002-001-002 | ǰ������ǿ��ָʾ�� | 2026-06-04T22:10 | ? | PasswordStrength.vue���(el-progress������+6��������+����ӳ����/��/ǿ+��ѡ��������+����?/?���)+passwordPolicy APIģ��+appStore����+��¼ҳ���� | 688a6266 |
| P0-004-011-002-002-001 | ʵ����������߼� | 2026-06-04T23:05 | ? | ���: UserService.checkPasswordExpired+AuthService��¼����+PasswordExpireTask��ʱ����+�����޸�ʱ����expire_date+��ʷ����У��(��3��); ǰ��: ChangePassword.vue+·������ǿ����ת+PASSWORD_EXPIRED_WHITE_LIST+userStore.passwordExpired״̬ | 3f3d1dc5 |
| P0-004-011-002-002-002 | ʵ����ʷ����У�� | 2026-06-04T23:45 | ? | 23�Ԫ����(UserServicePasswordHistoryTest)��֤��ʷ����У���߼�: ���ľܾ�/��������+�߽�����(����ʷ/null/1-3��)+����У��+�������ü�¼; ����1����Ϣ��ʧ�����Ѽ�¼ | (pending) |
| P0-004-011-003-001-001 | ʵ�ֵ�¼ʧ�ܼ��������� | 2026-06-05T08:05 | ? | ����LoginAttemptService(Redisԭ�Ӽ���+��̬����/TTL�״����ò���/�������+ʣ��ʱ��)/�ع�AuthServiceʹ���·���/����22�Ԫ����ȫ��ͨ�� | 58191d3e |
| P0-004-011-003-001-002 | ʵ������״̬��ѯ����� | 2026-06-05T09:30 | ? | ���: LoginAttemptService����getLockStatus/unlock����+LockStatusVO+AuthController����GET /auth/lock-status��POST /auth/unlock�˵�+UserServiceImpl.unlockUser����Redis����; ǰ��: useLogin����500ms������״̬��ѯ+��¼ҳ��������+�û�����ҳ������ť���������� | 3ae1ba8f |
| P0-004-012-002-001-002 | ��֤���� | 2026-06-05T11:30 | ? | SSO/OAuth2���ù�����֤: 24�����֤ȫ��ͨ��(SSO CRUD��5+OAuth2 CRUD��5+�������ӡ�5+AES���ܡ�5+֤��У���4)+mvn compileͨ��+auth 114�����ͨ��+test-report.md+issues.md(5����: 1��/2��/2��) | d094e4cc |
| P0-004-012-003-001-001 | ʵ�����ñ���UI | 2026-06-05T11:50 | ? | SSO/OAuth2���ù���ǰ��ҳ��: API���Ͷ���+APIģ��(SSO CRUD+OAuth2 CRUD+���Ӳ���)+Vueҳ��(SSO/OAuth2˫��ǩ+���ݱ���+�����Ի���+�ֶ�У��)+·��ע��+vite buildͨ�� | 91b18d1e |
| P0-004-012-003-001-002 | ʵ�ֻص�URL�Զ�������Ԥ�� | 2026-06-05T11:45 | ? | CallbackUrlInput.vue���: �ص�URL�Զ�����(computed��Ӧʽ)+providerTypeӳ��(wecom��wechat_work)+һ������(clipboard API+execCommand����)+HTTP������ʾ+�ȿ�����չʾ+������OAuth2���� | 8721ec82 |
| P0-004-012-003-002-001 | ʵ��OAuth2�������Ӱ�ť | 2026-06-05T12:15 | ? | ���: OAuth2TestResult VO(�ṹ�����Խ��)+OAuth2ConfigController.testConnection�ع�(GET����/5s+10s��ʱ/200+302+401�ж�/�������DNS+��ʱ+���Ӿܾ�); ǰ��: �����Ի���������Ӱ�ť(�༭ģʽ)/el-alert��ϸ��־չʾ(testUrl+statusCode+responseTime+message)/����ȡ����ť�������� | c3417e46 |
| P0-004-012-003-003-001 | ʵ�ֱ����߼� | 2026-06-05T12:45 | ? | �޸�RT.ok()��Ӧ��200��0����ErrorCode; ����URL��ʽУ��+ClientSecretǿ��У��+�༭ģʽ���ժҪȷ��+version�ֹ���+����ɹ�������Ч��ʾ+����״̬�������+��˴�����Ϣ͸��+loading���ظ��ύ; mvn compile + vue-tscͨ�� | 033e4a7d |

### P0-005 - ���������������

| ������ | �������� | ���ʱ�� | ״̬ | ժҪ | SHA |
|---------|---------|---------|:---:|------|-----|
| P0-005-003-008-001-003 | ��֤ǰ��ҳ�濪��ҳ�� | 2026-06-06T00:35 | ? | ��֤ListTable���6��ܣ�������/����/ɸѡ/��ҳ/�������/�п��־û������ͼ��ͨ����173����Ԫ����ȫ��ͨ�� | (��֤����) |
| P0-005-001-001-001-001 | ��д���Ĵ��� | 2026-06-05T12:55 | ? | ǰ��: QueryPanel���(11���ֶ�����/v-model/��������/���۵�/disabled/У���������); ���Ͷ���(FieldConfig/FieldControlType/LinkageRule); ��Ԫ����32��(������76.7%/��֧96%); vitest����Element Plusȫ��ע�� | 5da00902 |
| P0-005-001-001-001-002 | ��֤���� | 2026-06-05T19:20 | ? | ��֤QueryPanel���6���: �ֶ���Ⱦ/ģ����ѯ/�۵�չ��/����/��������/����; ��Ԫ����32/32ͨ��; vue-tsc���ͼ��ͨ�� | 24092d24 |
| P0-005-001-002-001-001 | ��д���Ĵ��� | 2026-06-05T20:05 | ? | ��ǿQueryPanel: �����placeholder prop/prefix suffix default���/try-catch������; ���Ͷ������; ����7������(106/106ͨ��) | 43cdad2f |
| P0-005-001-002-001-002 | ��֤���� | 2026-06-05T19:45 | ? | ��֤QueryPanel��ǿ����: 39/39����ͨ��; vue-tsc���ͼ��ͨ��; ����props/�¼�/���/�ֶ�����/�۵�/������ | d5fb3ddf |
| P0-005-001-003-001-001 | ��д���Ĵ��� | 2026-06-05T20:20 | ? | ʵ��QueryPanel�۵�/չ������: collapsible+collapseThreshold props/visibleFields computed/չ������ť; ����8���۵�����(107/107ͨ��) | 18a0eb23 |
| P0-005-001-003-001-002 | ��֤���� | 2026-06-05T20:32 | ? | ��֤QueryPanel�۵�չ��: 40/40����ͨ��; ����11���ֶ�����/�۵�չ��/����/�¼�/slots/�ֶ�����; vue-tscͨ�� | def9f184 |
| P0-005-002-001-001-001 | ��д���Ĵ��� | 2026-06-05T20:40 | ? | ActionBar���(���Ұ�ť��/�����˵�/ȷ�ϵ���/v-model); ���Ͷ���(ActionItem/ActionBarProps); useActionBar���ʽ����; APIģ��(batchDelete/batchAudit/export/import); vue-tsc�����+vite buildͨ�� | �� |
| P0-005-002-001-001-002 | ��֤���� | 2026-06-05T21:00 | ? | ��֤ActionBar���: 6������4��ͨ��(fieldConfig��Ⱦ/disabled����/���Ҳ���/�¼�emit); icon��Ⱦ��v-permission/permission�ֶ�/��Ӧʽ���Ϊ���������� | �� |
| P0-005-002-002-001-001 | ��д���Ĵ��� | 2026-06-05T21:10 | ? | ActionBar����ҳģʽ: mode prop(list/form)/form�Ҷ��벼��/getDefaultFormActions(�ύ/�ݸ�/�ύ������/����/ȡ��)/API�����ύ+����ݸ�+�����/������չ(BuiltInAction+ActionBarMode); vue-tsc�����+vite buildͨ�� | �� |
| P0-005-002-002-001-002 | ��֤���� | 2026-06-05T21:25 | ? | ActionBar�����֤: ������6��(��ť��Ⱦ/Ȩ�޼�Ȩ/��������/���Ҳ���/����۵�/�¼�emit); iconδ��Ⱦ+��v-permission+������۵�Ϊ��֪���; ���������; ������֤����VERIFICATION_REPORT.md | �� |
| P0-005-002-003-001-001 | ��д���Ĵ��� | 2026-06-05T21:55 | ? | HeaderToolbarͷ����չ������: Vue���(����/ˢ��/��ʽ����/�иߵ����������); ���Ͷ���(HeaderToolbarState/HeaderToolbarItem/RowHeightPreset��); 19����Ԫ����ȫ��ͨ��; TypeScript���ͼ������� | �� |
| P0-005-002-003-001-002 | ��֤���� | 2026-06-05T22:00 | ? | ��֤HeaderToolbar: 10����ȫ��ͨ��(������Ⱦ/v-model/change�¼�/focus-blur/disabled/hidden/�и�����/���/״̬�л�/���Ͱ�ȫ); 19/19����ͨ��; vue-tsc�����; ����VERIFICATION_REPORT.md | �� |
| P0-005-003-001-001-001 | ��д���Ĵ��� | 2026-06-05T22:10 | ? | ListTable�б��������: Vue���(����Ⱦ/���������/��ɸѡ/��ҳ/�������/�п��־û�/������/�߿�/�и���); ���Ͷ���(ListTableColumn/FilterConfig/SortConfig��); 21����Ԫ����ȫ��ͨ��; vue-tsc����� | 2c4503dc |
| P0-005-003-001-001-002 | ��֤���� | 2026-06-05T22:15 | ? | 21��vitest����ȫ��ͨ��; vue-tsc���ͼ�������; ������֤:��������Ⱦ/���������/��ɸѡ(�ı���������)/ǰ�˷���˷�ҳ/�������/�п�localStorage�־û�; vxe-table^4.19.4����4.xҪ�� | def5e354 |
| P0-005-003-002-001-001 | ��д���Ĵ��� | 2026-06-05T22:30 | ? | ����������߼�: SortConfig��ǿ(��������fields����/multiple/trigger/remote/showIcon); SortField�ӿ�; setSort/getSortColumns���ʽ�������; handleSortChange��������״̬׷��; 32������ȫ��ͨ��(����10������ר��) | a074027a |
| P0-005-003-002-001-002 | ��֤���� | 2026-06-05T23:25 | ? | ��������֤: 32/32����ͨ��; vue-tsc���ͼ�������(��list-table���); ������֤:��������Ⱦ/���������(remote/multi/trigger)/��ɸѡ(�ı���������)/��ҳ�л�(via pageMode)/�������(Ĭ�Ͽ���)/�п�localStorage�־û�(viewCodeǰ׺); vxe-table 4.19.4����4.xҪ��; ��������ܲ���(��Ⱦ/FPS/Network)���ֶ���֤ | b0ec41cf |
| P0-005-003-003-001-001 | ��д���Ĵ��� | 2026-06-05T21:36 | ? | ������ɸѡ�����߼�: searchModel v-model/searchModel��; disabled prop; prefix/suffix���; currentFilterModel״̬����; setFilter/getFilterColumns���ʽɸѡ����; clearFilter��ǿ(֧�ְ��ֶ����); change/focus/blur�¼�; ���Ͷ���(ListTableSearchModel/FilterColumnInfo/ListTableSlots/ListTableExpose); 40����Ԫ����ȫ��ͨ��; vue-tsc����� | f44f2022 |
| P0-005-003-003-001-002 | ��֤���� | 2026-06-05T21:48 | ? | ��֤ListTable������ɸѡ����: 40/40����ͨ��; vue-tsc�����; ��������Ⱦ/���������/��ɸѡ(text-number-date)/��ҳ�л�(pageMode)/�������/�п�localStorage�־û�6��ȫ��ͨ��; ��������ܲ���(FPS/��Ⱦʱ��)���ֶ���֤ | f44f2022 |
| P0-005-003-004-001-001 | ��д�����ļ��� | 2026-06-05T21:48 | ? | ���: ListTableProperties������(@ConfigurationProperties/list-tableǰ׺/@ValidatedУ��/ColumnFormat+ColumnDefault+ColumnPersistǶ������); application.yml����list-table���ÿ�(�и�ʽ/��Ĭ��ֵ/�г־û�) | �� |
| P0-005-003-004-001-002 | ��֤��д���������� | 2026-06-05T22:00 | ? | ��֤ListTableProperties����: YAML�﷨��ȷ/����ͨ��/dev-prod�����޳�ͻ/��������ϢӲ����/@Component+@ConfigurationProperties��ȷ��/@ValidatedУ��ע������/prefixȫСд�л��߷ָ� | �� |
| P0-005-003-005-001-001 | ��д�����ļ��� | 2026-06-05T22:10 | ? | ���: ListTableProperties����RowHeight(7���и�px)/FontSize(7������px)Ƕ������; application.yml����list-table.row-height+font-size���ÿ� | 8b2ab37d |
| P0-005-003-005-001-002 | ��֤��д���������� | 2026-06-05T22:35 | ? | ��֤ListTableProperties�и�/�����С����: YAML�﷨��ȷ/mvn compileͨ��/��������Ϣ/@Component+@ConfigurationProperties��ȷ/@Validated/prefixȫСд�л���/7��������Ĭ��ֵһ�� | �� |
| P0-005-003-006-001-001 | ��д���Ĵ��� | 2026-06-05T23:15 | ? | ListTableһ����ʼ��/һ�������������: modelValue/fieldConfig/placeholder props; update:modelValue/focus/blur�¼�emit; resetAll()/clearSearchAndSort()����; ���Ͷ���(FieldConfig/FieldValidationRule/FieldLinkageRule); 47������ͨ��; vue-tsc����� | �� |
860	| P0-005-003-006-001-002 | ��֤���� | 2026-06-05T23:18 | ? | ��֤ListTableһ����ʼ��/һ�������������: 47/47����ͨ��; vue-tsc�����; ������֤:��������Ⱦ/���������/��ɸѡ/��ҳ�л�/�������/�п�localStorage�־û�/resetAll+clearSearchAndSort������¶; ��������ܲ���(FPS/��Ⱦ)���ֶ���֤ | �� |
| P0-005-003-007-001-001 | ��д�����ļ��� | 2026-06-05T23:35 | ? | ListTable�ϼ������������Ͷ���: SummaryMethod/SummaryColumnConfig/SummaryConfig����; ListTableColumn.summary�ֶ�; ListTableProps.summaryConfig/SummaryData | 3cf16dc0 |
| P0-005-003-007-001-002 | ��֤��д���������� | 2026-06-05T23:42 | ? | ��֤ListTable�ϼ�������������: SummaryConfig/SummaryColumnConfig/SummaryMethod�����﷨��ȷ; 47/47����ͨ��; ��������Ϣ; ����ͨ��@/types/list-table��ȷ���� | �� |
| P0-005-003-008-001-001 | ��дҳ����� | 2026-06-05T23:55 | ? | �б�����������书����ʾҳ: Vue3 page���(template+script setup+style scoped); DemoOrderItem���Ͷ���+APIģ��(mock 156������/����/����/��ҳ); useDemoListTable���ʽ����(columns/sortConfig/summaryData/�¼�����); ·��ע��/demo/list-table; vue-tsc�����+vite buildͨ�� | �� |
| P0-005-003-008-001-002 | ��д���ݰ��뽻���߼� | 2026-06-06T00:15 | ? | ���ݰ��뽻���߼�: saveDemoOrder/deleteDemoOrder/getDemoOrderDetail mock API; useDemoListTable����error/saving/deleting״̬+handleSave/handleDelete/handleViewDetail+router����+watch 300ms����+syncQueryToState; index.vue��������/�༭/ɾ����ť+el-dialog����+el-alert����չʾ+·��queryͬ�� | �� |
| P0-005-004-001-001-001 | ��д���Ĵ��� | 2026-06-06T01:10 | ? | EntryTable¼�����ݱ������: Vue SFC(vxe-grid�ɱ༭��װ/edit-config��Ԫ��༭/no-pagination/ǰ�˺ϼƼ���/�г־û�); ���Ͷ���(EditTableColumn/EditRule/EditChangeParams��); 41����Ԫ����ȫ��ͨ��; vue-tsc�����+vite buildͨ�� | (���ύ) |
| P0-005-004-001-001-002 | ��֤���� | 2026-06-06T01:20 | ? | ��֤EntryTable���: 41/41����ͨ��; vite buildͨ��(5.63s); 6����֤:��Ԫ��༭/У��/Tab����/�й���4��ͨ��; paste-config��row-drag 2��δ��ʽ��������vxe-tableĬ����Ϊ | (��֤����) |
| P0-005-004-002-001-001 | ��д���Ĵ��� | 2026-06-06T01:25 | ? | ����ק������Ĵ���: DragConfig���Ͷ���/dragConfig prop/dragConfigValue computed/handleDragSort����/reorder����/10���²���(51/51ͨ��)/vue-tsc�����/vite buildͨ�� | (���ύ) |
| P0-005-004-002-001-002 | ��֤���� | 2026-06-06T02:30 | ? | ��֤����ק����: 51/51����ͨ��(��7��dragר��); 6����֤�嵥ȫͨ��(�༭/У��/�й���/Tab/ճ��/��ק); �������ȷ��editConfig/dragConfig/editRules/validate/reorder����ʵ�� | (���ύ) |
| P0-005-004-003-001-001 | ��д�����ļ��� | 2026-06-06T02:15 | ? | ���: EditTableProperties������(@ConfigurationProperties/edit-tableǰ׺/@ValidatedУ��/ColumnFormat+ColumnDefault+ColumnPersist+EditǶ������); application.yml����edit-table���ÿ�(�и�ʽ/��Ĭ��ֵ/�г־û�/�༭����) | b4fa0e07 |
| P0-005-004-003-001-002 | ��֤��д���������� | 2026-06-06T02:30 | ? | ��֤edit-table����: YAML�﷨��ȷ/����ͨ��/���԰���ȷ/�����̳���ȷ/��������Ϣ/@Componentע���ע | 227934ab |
| P0-005-004-004-001-001 | ��д�����ļ��� | 2026-06-06T03:00 | ? | ���: EditTableProperties����RowHeight(7���и�px)/FontSize(7������px)Ƕ������; application.yml����edit-table.row-height+font-size���ÿ� | b142f047 |
| P0-005-004-004-001-002 | ��֤��д���������� | 2026-06-06T03:30 | ? | ��֤EditTableProperties�и�/�����С����: YAML�﷨��ȷ/mvn compileͨ��/��������Ϣ/@Component+@ConfigurationProperties��ȷ/@Validated/prefixȫСд�л���/7��������Ĭ��ֵһ�� | (���ύ) |
| P0-005-004-005-001-001 | ��дҳ����� | 2026-06-06T03:50 | ? | ¼�����ݱ���ϼ�����ʾҳ: ErpEditTable���demoҳ��(useDemoEditTable composable/�ϼ�������/summaryConfig/����ק/��Ԫ��༭/·��ע��); vue-tsc�����; vite buildͨ�� | (���ύ) |
| P0-005-004-005-001-002 | ��д���ݰ��뽻���߼� | 2026-06-06T04:05 | ? | API���ò�(src/api/modules/edit-table.ts: EditTableRow����/CRUD/��ҳ/mock����); composable�ع�(API����/useRouter·�ɵ���/useRoute������ȡ/searchParams+watch 300ms����/loading״̬/�첽������); demoҳ������v-loadingָ��; vue-tsc�����; 51���������ȫ��ͨ�� | c6cf486b |
| P0-005-004-005-001-003 | ��֤ǰ��ҳ�濪��ҳ�� | 2026-06-06T04:30 | ? | ��֤EntryTable�ϼ�����ʾҳ: 6����֤�嵥ȫͨ��(��Ԫ��༭/У��/�й���/Tab����/Excelճ��/��ק����); �������editConfig/dragConfig/editRules/summaryDataʵ������; �޸��������ʹ���(FieldConfig����·��+defineExpose��¶�ڲ�״̬); edit-table���vue-tsc����� | (���ύ) |
| P0-005-004-006-001-001 | ��д�����ļ��� | 2026-06-06T00:45 | ? | EditTableProperties����CellRenderǶ������(cell-renderǰ׺/5������/���ӳ��7��); application.yml����edit-table.cell-render���ÿ�(Ĭ�����/����Ⱦ/����/���ӳ��); mvn compileͨ�� | (���ύ) |
| P0-005-004-006-001-002 | ��֤��д���������� | 2026-06-06T01:00 | ? | ��֤edit-table.cell-render����: YAML�﷨��ȷ/mvn compileͨ��/�������̳���ȷ/��������Ϣ/@Component+@ConfigurationProperties��ȷ��/prefixȫСд�л���/cellRenderǶ��+componentMappingӳ�����֤ͨ�� | (��֤����) |
| P0-005-004-007-001-002 | ��д���ݰ��뽻���߼� | 2026-06-06T01:30 | ? | ��ǿuseDemoEditTableReadonly composable: handleSearch(��ѯ������װ)/loadDetail(getEditTableDetail����)/ElMessageBoxɾ��ȷ�ϵ���/handleSave����У�鼯��(validateFn) ; readonly-demo.vue: useRoute��ȡdetailId�Զ���������/onSave��װУ��/onMounted�첽��ʼ��; vue-tsc�����(����ǰ���); ����CRUD�������������ջ� | (���ύ) |
| P0-005-004-007-001-003 | ��֤ǰ��ҳ�濪��ҳ�� | 2026-06-06T01:20 | ? | ��֤EntryTableֻ��/����̬: 51/51����ͨ��; 6����֤: ��Ԫ��༭?/У��?/�й���?/Tab����?/Excelճ��??δʵ��/��ק����?; ���Ĺ���disabled��beforeEditMethod+��������������֤ | (��֤����) |
| P0-005-005-001-001-001 | ��д���Ĵ��� | 2026-06-06T00:36 | ? | GroupNav�����鵼�������: Vue SFC(������Ⱦ/չ���۵�/�ӷ���/ͼ��/�Ǳ�/v-modelѡ��/disabled����/prefix-suffix-default���); ���Ͷ���(NavGroup/GroupNavProps/GroupNavEmits/GroupNavExpose/RelatedTab/RelatedInfoAreaProps��); 34����Ԫ����ȫ��ͨ��; vue-tsc����� | 0e918fa2 |
| P0-005-005-001-001-002 | ��֤���� | 2026-06-06T00:42 | ? | ��֤GroupNav�����鵼����: 34/34����ͨ��(��Ⱦ/���/v-model/չ���۵�/��¶����/�߽����); vue-tsc�����ʹ���; v-if������ȷ��; 6����֤: ���鵼��չ���۵�?/��ǩҳ����?(���ͼ�)/Ȩ�޿���?(���ͼ�)/����ˢ��?(���ͼ�)/�л�ˢ��?(���ͼ�)/ͷ��������?(���ͼ�) | b9ccf344 |
| P0-005-005-002-001-001 | ��д���Ĵ��� | 2026-06-06T00:50 | ? | TabContainer�Ҳ��ǩҳ�������: Vue SFC(��ǩҳ��Ⱦ/activeGroupɸѡ/v-modelѡ��/disabled����/prefix-suffix-default���/��¶����); ���Ͷ���ʹ������RelatedTab; 31����Ԫ����ȫ��ͨ��(������96.87%); vue-tsc����� | 97b697a2 |
| P0-005-005-002-001-002 | ��֤���� | 2026-06-06T01:00 | ? | ��֤TabContainer: 31/31+34/34=65/65����ͨ��; vue-tsc�����; vite buildͨ��(5.62s); 6����֤: ���鵼��?/��ǩҳ�л�?/Ȩ�޿���?/����ˢ��API?/�л�ˢ��API?/���������? | (��֤����) |
| P0-005-005-003-001-001 | ��д���Ĵ��� | 2026-06-06T01:05 | ? | ��ǩҳ����Ȩ������֧��: useTabPermission���ʽ����(Ȩ�޹���/activeGroupɸѡ/hidden����/canAccessTab���/permissionHiddenCountͳ��); TabContainer����Ȩ�޹���; 14�����ʽ��������+31���������ȫ��ͨ��; vue-tsc����� | 6c75bfd8 |
| P0-005-005-003-001-002 | ��֤���� | 2026-06-06T01:15 | ? | ��֤��ǩҳ����Ȩ������֧��: useTabPermission 14/14����ͨ��; TabContainer 31/31����ͨ��; GroupNav 34/34����ͨ��; vue-tsc 316���ͼ�������; 3�������֤(���鵼��/��ǩҳ����/��ǩҳȨ��)ȫ��ͨ�� | (��֤����) |
| P0-005-005-004-001-001 | ��д���Ĵ��� | 2026-06-06T01:25 | ? | RelatedInfoArea������Ϣ�������: Vue SFC(GroupNav+TabContainer����/����ˢ�»���/provide-injectˢ��������/mainRow�����Զ�ˢ��/�����л��Զ�ѡtab); ���Ͷ���(RelatedInfoRefreshContext); 47����Ԫ����ȫ��ͨ��; ��ع�(112/112) | 972030f0 |
| P0-005-005-004-001-002 | ��֤���� | 2026-06-06T01:35 | ? | ��֤RelatedInfoArea�������: ����47����Ԫ����ȫ��ͨ��(���鵼��/��ǩҳ�л�/Ȩ�޹���/v-model/mainRowˢ��/expose����/���/�߽�����); 6������ȫ��ͨ�� | ae8e23a7 |
| P0-005-005-005-001-001 | ��д���Ĵ��� | 2026-06-06T01:40 | ? | HeaderToolbarͷ����չ������(relation-info): Vue SFC(����/ˢ��/��ʽ����/�иߵ���); ���Ͷ���(RelatedInfoToolbarState/Item/Tool/RowHeightPreset); 29����Ԫ����ȫ��ͨ��; vue-tsc����� | dc18f33a |
| P0-005-005-005-001-002 | ��֤���� | 2026-06-06T02:00 | ? | ��֤RelatedInfoArea+HeaderToolbar��������: ����76����Ԫ����ȫ��ͨ��; ����19����֤�������Ƿ���չ���۵�/������/Ȩ�޿���/��ǩ�л�ˢ��/HeaderToolbar����; ��ع� | 444fa92c |
| P0-005-006-001-001-001 | ��д���Ĵ��� | 2026-06-06T02:00 | ? | DetailTableArea��ϸ�ӱ��������: Vue SFC(��ǩҳ����/������/fieldConfig����/v-modelѡ��/disabled����/prefix-suffix-default���); ���Ͷ���(DetailTableTab/DetailTableProps/DetailTableEmits/DetailTableSlots/DetailTableExpose); 21����Ԫ����ȫ��ͨ��; vue-tsc�����; vite buildͨ�� | da57875b |
| P0-005-006-001-001-002 | ��֤���� | 2026-06-06T03:05 | ? | ��֤DetailTableArea����ǩҳ����: 21/21����ͨ��; vue-tsc���ͼ�������; ��֤��:��ǩҳ�л�?/������?(lazy����)/���ع���?/disabled?/���?/focus-blur�¼�?/expose����?; ͷ��������+��������Ϊsub-module 002/003��Χ | (��֤����) |
| P0-005-007-004-001-002 | ��֤���� | 2026-06-06T03:25 | ? | ��֤MasterForm������������: 58/58��Ԫ����ͨ��(useFormLinkage 27+FormField 31); vue-tsc --noEmit�����; 6����֤�嵥ȫͨ��(����У��/�ֶ�����/�Զ�����/�ύ����/��������/�ֶ���������); �������ͼ���ֶ���֤ | (��֤����) |
| P0-005-006-002-001-001 | ��д���Ĵ��� | 2026-06-06T02:10 | ? | DetailTableArea HeaderToolbarͷ����չ������: Vue SFC(����������/�����л�/ˢ��/�иߵ���); ���Ͷ���(DetailTableToolbarItem/DetailTableToolbarState/DetailTableRowHeightPreset��); 36����Ԫ����ȫ��ͨ��(������88.29%/��֧88.31%); vue-tsc�����+vite buildͨ�� | 3e65078d |

| P0-005-006-002-001-002 | ��֤���� | 2026-06-06T02:15 | ? | ��֤DetailTableArea HeaderToolbarͷ����չ������: 61����Ԫ����ȫ��ͨ��(57����+4����������) | 80a05e3a |
| P0-005-006-003-001-001 | ��д���Ĵ��� | 2026-06-06T04:35 | ? | ʵ��DetailTableArea���������л�: ����maximized prop+CSS������ʽ+toggleMaximize����+maximize/unmaximize�¼�; 31����Ԫ����ȫ��ͨ��(25����+6���������л�) | �� |
| P0-005-006-003-001-002 | ��֤���� | 2026-06-06T02:30 | ? | ��֤DetailTableArea���������л�: 67/67��Ԫ����ȫ��ͨ��(index+HeaderToolbar); vue-tsc�����; ������֤��ǩҳ�л�/������/����CSS��/maximize-unmaximize�¼�/����������/�иߵ���/disabled/toggleMaximize/��� | (��֤����) |
| P0-005-007-001-001-001 | ��д�����ļ��� | 2026-06-06T03:05 | ? | ǰ��: MasterForm+FormField���Ͷ���(master-form.d.ts): FormFieldConfig(20��fieldType)/FormLayoutConfig(����ģʽ/��ǩ/դ��/����)/FormFieldProps-Emits-Slots-Expose/MasterFormProps-Emits-Slots-Expose; ��������˵���ĵ�(JSDoc����); tsc --noEmit����� | b20e23cb |
| P0-005-007-001-001-002 | ��֤��д���������� | 2026-06-06T03:35 | ? | ��֤MasterForm+FormField���Ͷ���: �﷨��ȷ(tsc�����)/����·����ȷ/20��fieldType����/Props-Emits-Slots-Exposeȫ������淶/��������Ϣ/12 interface+4 typeȫ��export | 29794547 |
| P0-005-007-002-001-001 | ��д�����ļ��� | 2026-06-06T04:05 | ? | ǰ��: ���������������Ͷ���(form-layout.d.ts): ResponsiveFormGridConfig/FormTabLayoutConfig/FormStepLayoutConfig/FormSectionLayoutConfig/FormRowLayoutConfig/FormLayoutExtendedConfig/FieldLayoutPosition/FormLayoutResult/FormLayoutPresets��������; ����JSDoc������˵���ĵ�; tsc --noEmit����� | 28caf85e |
| P0-005-007-002-001-002 | ��֤��д���������� | 2026-06-06T04:30 | ? | ��֤������������: tsc�����; ����form-layout.ts����ʱʵ��(FORM_LAYOUT_PRESETS 6��Ԥ��); �﷨��ȷ/��������Ϣ/���ÿ�������ȡ | �� |

| P0-005-007-003-001-001 | ��д���Ĵ��� | 2026-06-06T05:05 | ? | ����У���������: useFormValidation���ʽ����(5��У�����)+FormField.vue(20��fieldTypeӳ��)+MasterForm.vue(У������+����դ�񲼾�); 31����/82.5%������/vue-tsc�����/vite buildͨ�� | 2116bbce |

| P0-005-007-003-001-002 | ��֤���� | 2026-06-06T05:15 | ? | ��֤����У������: 31����ͨ��/vue-tsc�����/vite buildͨ��; �����ֶ���������(processLinkages: show/hide/setValue/setOptions); ����validate�۽��׸������ֶ�; FieldLinkageRule�����ص��� | (��֤����) |

| P0-005-007-004-001-001 | ��д���Ĵ��� | 2026-06-06T03:18 | ? | ���������������: useFormLinkage���ʽ����(6����������+����+ѭ�����+��������); MasterForm.vue����(enable/disable����+disabled״̬�ϲ�); 27����Ԫ����ȫ��ͨ��; vue-tsc����� | (���ύ) |

| P0-005-008-001-001-001 | ��д���Ĵ��� | 2026-06-06T03:28 | ? | TabPageContainer��ǩҳ�������: TabPageContainer.vue(����������Ⱦ/v-model˫���/activeGroupɸѡ/prefix+suffix+default�����/disabledģʽ); 34����Ԫ����ȫ��ͨ��; tab-container.d.ts���Ͷ���(TabItem/TabPageContainerProps/TabPageContainerEmits/TabPageContainerExpose) | (���ύ) |

| P0-005-008-001-001-002 | ��֤���� | 2026-06-06T03:36 | ? | ��֤TabPageContainer���: 34/34��Ԫ����ȫ��ͨ��; vue-tsc�����; 6����֤�嵥(��ǩҳ�л�/������/Ȩ�޿���/active tab�־û�/��ǩҳ���/tab�ر�)ȫ��ͨ��; �޸�permissionHiddenCountӲ����Ϊ0��bug | 783ee4d6 |

| P0-005-008-002-001-001 | ��д�����ļ��� | 2026-06-06T03:45 | ? | TabPageContainer����usePermissionȨ�޿���: visibleTabs����Ȩ�޹���/ permissionHiddenCount��Ϊͳ����Ȩ������/findTab����Ȩ�޼��; 44����Ԫ����ȫ��ͨ��(34����+10����Ȩ��); vue-tsc����� | (���ύ) |

| P0-005-008-002-001-002 | ��֤��д���������� | 2026-06-06T04:00 | ? | ��֤TabPageContainer����: 89����Ԫ����ȫ��ͨ��(TabPageContainer 44+useTabPermission 14+relation-info/TabContainer 31); vue-tsc�����; TabItem���Ͷ�������; Props/Events/Slots/expose����һ��; ��Ӳ����������Ϣ | fdd9edd4 |

| P0-005-009-001-001-001 | ��д���Ĵ��� | 2026-06-06T04:05 | ? | PageP01Dashboard���Ĵ���: page-base.d.ts���Ͷ���(PageBaseProps/Emits/Slots+PageConfig); PageP01Dashboard.vue(��ӭ��/KPI��Ƭ/������/ͼ������/������Ϣ/�������6������+���֧��); 32����Ԫ����ȫ��ͨ��; vue-tsc����� | (���ύ) |

| P0-005-009-001-001-002 | ��֤���� | 2026-06-06T04:08 | ? | ��֤PageP01Dashboard���: 32/32��Ԫ����ȫ��ͨ��; vue-tsc�����; 6����֤�嵥(��Ⱦ/Props/Events/Slots/��Ӧʽ����/�߽�����)ȫ��ͨ�� | 89191ac6 |

| P0-005-009-002-001-001 | ��д���Ĵ��� | 2026-06-06T04:15 | ? | PageP02Workbench���Ĵ���: page-base.d.ts����WorkbenchPageConfig/WorkbenchStatCardConfig����; PageP02Workbench.vue(����̨������/ͳ�ƿ�Ƭ��/��ѯ��/������/��������5������+5�����); 35����Ԫ����ȫ��ͨ�� | be141fe4 |

| P0-005-009-002-001-002 | ��֤���� | 2026-06-06T04:22 | ? | ��֤PageP02Workbench���: 35/35��Ԫ����ȫ��ͨ��; vue-tsc�����; vite build�ɹ�; 6����֤�嵥(���񲼾�/���ݸ���/���ֿ�����/��������/�¼�ͨ��/Slots)ȫ��ͨ�� | 6d6153e7 |

| P0-005-009-003-001-001 | ��д���Ĵ��� | 2026-06-06T04:28 | ? | PageP03MasterList���Ĵ���: page-base.d.ts����MasterListPageConfig����; PageP03MasterList.vue(��ѯ��/������/���ҷ������Ӳ���+5�����); 27����Ԫ����ȫ��ͨ��; vue-tsc����� | 3c620cfe |

| P0-005-009-003-001-002 | ��֤���� | 2026-06-06T05:00 | ? | ��֤PageP03MasterList���: 27/27��Ԫ����ȫ��ͨ��; vue-tsc�����; 6����֤�嵥(��ѯ��/���б���/������Ϣ��/�����л�/��������/��������)ȫͨ�� | �� |

| P0-005-009-004-001-001 | ��д���Ĵ��� | 2026-06-06T04:40 | ? | PageP04SimpleList���Ĵ���: page-base.d.ts����SimpleListPageConfig; PageP04SimpleList.vue(��ѯ��/������/ȫ�����б���+4�����); 23����Ԫ����ȫ��ͨ��; vue-tsc����� | (���ύ) |
| P0-005-009-004-001-002 | ��֤���� | 2026-06-06T05:05 | ? | ��֤PageP04SimpleList���: 23/23��Ԫ����ȫ��ͨ��; vue-tsc�����; vite buildͨ��; 6����֤�嵥(��ѯ��/���б���/��ҳ/��������/����/��Ӧʽ)ȫ�����ͨ�� | (��֤����) |

| P0-005-009-005-001-001 | ��д���Ĵ��� | 2026-06-06T04:55 | ? | PageP05TreeList���Ĵ���: page-base.d.ts����TreeListPageConfig����; PageP05TreeList.vue(��ѯ��/������ε���280px+����/�Ҳ������+�����б�+4����׼���+3��P05ר�в��); 30����Ԫ����ȫ��ͨ��; vue-tsc����� | dbdf31e3 |
| P0-005-009-005-001-002 | ��֤���� | 2026-06-06T05:00 | ? | ��֤PageP05TreeList: 30����Ԫ����ȫ��ͨ��; vue-tsc���ͼ�������; vite build����ͨ�� | �� |

| P0-005-009-006-001-001 | ��д���Ĵ��� | 2026-06-06T05:10 | ? | PageP06MasterForm���Ĵ���: page-base.d.ts����MasterFormPageConfig����; PageP06MasterForm.vue(��ѯ��/������/������������960px+4�����); 22����Ԫ����ȫ��ͨ��; vue-tsc����� | 7d85f20d |
| P0-005-009-006-001-002 | ��֤���� | 2026-06-06T05:15 | ? | ��֤PageP06MasterForm: 22/22��Ԫ����ȫ��ͨ��; vue-tsc�����; 733/733ȫ������ͨ��; 6����֤�嵥(���ܲ�����/��������Ⱦ/��ϸ�ӱ�/��������/�����л�/������Ϊ)ȫ�����ͨ�� | �� |

| P0-005-009-007-001-001 | ��д���Ĵ��� | 2026-06-06T05:12 | ? | PageP07SimpleForm���Ĵ���: page-base.d.ts����SimpleFormPageConfig����; PageP07SimpleForm.vue(��ѯ��/������/������������960px+4�����); 22����Ԫ����ȫ��ͨ��; vue-tsc����� | 45f8f010 |

| P0-005-009-007-001-002 | ��֤���� | 2026-06-06T05:19 | ? | ��֤PageP07SimpleForm: 22/22��Ԫ����ȫ��ͨ��; ȫ��191����ͨ��(P01-P07��7���); vue-tsc�����; vite build�ɹ�; 6����֤�嵥(���ܲ�����/��������Ⱦ/����У��/������Ϊ/�ر���Ϊ/����ģʽ)ȫ�����ͨ�� | �� |

| P0-005-009-008-001-001 | ��д���Ĵ��� | 2026-06-06T05:25 | ? | PageP08Kanban���Ĵ���: page-base.d.ts����KanbanPageConfig/KanbanColumnConfig/KanbanItemConfig+����PageConfig��������; PageP08Kanban.vue(��ѯ��/������/������+��Ƭ+��ɫ����/empty/���); 35����Ԫ����ȫ��ͨ��; vue-tsc����� | �� |
| P0-005-009-008-001-002 | ��֤���� | 2026-06-06T05:35 | ? | ��֤PageP08Kanban: 35/35����ͨ��; vue-tsc�����; vite build�ɹ�(5.64s); 6����֤�嵥(Ӿ����Ⱦ/��Ƭ��Ⱦ/����¼�/Ȩ�޿���/���/�߽�����)ȫ��ͨ�� | (���ύ) |
| P0-005-009-009-001-001 | ��д���Ĵ��� | 2026-06-06T06:10 | ? | page-base.d.ts����QueryPageConfig/QueryFieldConfig����+����PageConfig��������; PageP09Query.vue(��ѯ������������Ⱦ/������/�������/��������/��ҳ��/extra-area+6�����); 33����Ԫ����ȫ��ͨ��; vue-tsc����� | (���ύ) |
| P0-005-009-009-001-002 | ��֤���� | 2026-06-06T05:50 | ? | PageP09Query������֤: 33/33����ͨ��; vue-tsc�����; ȫ��9��page-base���259/259����ͨ��; ��֤�嵥6����4��PASS+2��N/A(��������ҳ�淶Χ) | 118c8055 |
| P0-005-009-010-001-001 | ��д���Ĵ��� | 2026-06-06T06:00 | ? | page-base.d.ts����ReportPageConfig+ReportFilterConfig+ReportColumnConfig+ReportTreeConfig+ReportLedgerConfig����+����PageConfig��������; PageP10Report.vue(���������������/��������ӡ����/3�ֱ�������table+tree+ledger/5�����); 47����Ԫ����ȫ��ͨ��; vue-tsc����� | 20365f1a |
| P0-005-009-010-001-002 | ��֤���� | 2026-06-06T06:15 | ? | ��֤PageP10Report: 47/47��Ԫ����ȫ��ͨ��; vue-tsc�����; 6����֤�嵥(������/����չʾ/��͸��ȡ�¼�/ͼ�����/��������/��ӡ����)ȫ�����ͨ�� | 727c9671 |
| P0-005-009-011-001-001 | ��д���Ĵ��� | 2026-06-06T06:05 | ? | page-base.d.ts����ScreenPageConfig+ScreenKpiConfig+ScreenChartItemConfig����+����PageConfig��������; PageP11Screen.vue(��ɫ����/ͼ������/��ʱˢ��/ȫ���л�/��Ӧʽ����/KPI��ֵ����/4�����); 41����Ԫ����ȫ��ͨ��; vue-tsc����� | d945d4b3 |
| P0-005-009-011-001-002 | ��֤���� | 2026-06-06T06:20 | ? | ��֤PageP11Screen: 41/41����ͨ��; vue-tsc�����; 6����֤�嵥(��ɫ����/ͼ������/��ʱˢ��/ȫ���л�/��Ӧʽ����/���ݶ�̬Ч��)ȫ��ͨ�� | (��֤����) |
| P0-005-010-003-001-003 | ʵ������߼� | 2026-06-06T10:00 | ? | ErpNumberInput����߼�: ����displayValue��������(ǧ��λ��ʽ��); 29/29��Ԫ����ȫ��ͨ��; vue-tsc�����; vite build�ɹ�; ��Ӧʽ״̬/computed/watch/�¼�����/������������ | (���ύ) |

## ����ͳ��

| ���ȼ� | ģ���� | Ҷ���������� | ����� | ������ | ����� |
|:-----:|:-----:|:----------:|:-----:|:-----:|:-----:|
| P0 | 14 | 2,147 | 555 | 2 | 25.9% |
| P1 | 15 | 1,464 | 0 | 0 | 0.0% |
| P2 | 17 | 1,105 | 0 | 0 | 0.0% |
| **�ϼ�** | **46** | **4,716** | **553** | **2** | **11.7%** |

---

## ά������

1. **д��ʱ��**�������Լ�ͨ��������׷��
2. **ֻ����ɾ**���鵵��¼һ��д�벻��ɾ���������������
3. **��������**�����Ǳ�š����ơ�ʱ�䡢״̬��ժҪ��SHA����ֹд������ҵ��ϸ��
4. **ģ��ֽ�**����ĳģ����ɼ�¼���� 50 ��ʱ���ڸ�ģ�������ڰ����������С��
5. **ͳ��ͬ��**��ÿ����������µײ����ܱ�

### ģ�����: P0-004 ?

| P0-005-009-012-001-001 | ��д���Ĵ��� | 2026-06-06T06:25 | ? | PageP12Profile��������ҳ����������Vue���+40�����+���Ͷ��壩 | b3dca34c |
| P0-005-009-012-001-002 | ��֤���� | 2026-06-06T06:35 | ? | ��֤PageP12Profile�����40/40����ͨ����387/387ȫpage-base����ͨ����vue-tsc����ͨ�� | |
| P0-005-009-013-001-001 | ��д�����ļ��� | 2026-06-06T07:00 | ? | page-base.d.ts����ConfigPageConfig+ConfigFormFieldConfig+ConfigGroupConfig+ConfigNavItemConfig���ͣ�PageConfig�������͸��£�vue-tsc����� | c2daf19d |
| P0-005-009-013-001-002 | ��֤��д���������� | 2026-06-06T07:20 | ? | ��֤P13�������ͣ�vue-tsc�������ConfigPageConfig���TS���󣩣�.env���ļ��﷨��ȷ��Ӳ����������Ϣ��ConfigPageConfig����ȷ������PageConfig�������Ϳ��������� | (���ύ) |
| P0-005-009-014-001-001 | ��д���Ĵ��� | 2026-06-06T07:36 | ? | PageP14AIDialog AI�Ի�ҳ����������Vue������Ի���+���չʾ��+��ʽ���+��ʷ��¼+�ж�/�������ɣ�+25�Ԫ����+5��TypeScript���Ͷ��壨AIDialogPageConfig/AIDialogMessageConfig/AIResultType/AIDialogHistoryConfig����vue-tsc�����buildͨ����25/25����ͨ�� | (���ύ) |
| P0-005-009-014-001-002 | ��֤���� | 2026-06-06T06:42 | ? | ��֤PageP14AIDialog�����25/25����ͨ����13��page-base���412/412ȫ������ͨ����vue-tsc���ͼ�������vite build�ɹ�(5.76s)��6����֤�嵥(�Ի���/SSE��ʽ/���չʾ��/��ʷ��¼/�ж�����/��������)ȫ��ͨ�� | (���ύ) |
| P0-005-009-015-001-001 | ��д���Ĵ��� | 2026-06-06T06:52 | ? | PageP15Designer�����ҳ����������Vue�������������+������/����/�������+��ק����+����ɸѡ+�����ɾ�ƶ�+�������ã�+24�Ԫ����+3��TypeScript���Ͷ��壨DesignerPageConfig/DesignerComponentItemConfig/DesignerAvailableComponentConfig����vue-tsc�����buildͨ����24/24����ͨ�� | (���ύ) |
| P0-005-009-015-001-002 | ��֤���� | 2026-06-06T07:00 | ? | ��֤PageP15Designer�����24/24��Ԫ����ȫ��ͨ����vue-tsc --noEmit�����ʹ���6����֤�嵥(������/������/����/�������/��ק���/���Ա༭)ȫ��ͨ����page-ready�¼���ȷemit��canvas-placeholder��״̬��ȷ��Ⱦ | (���ύ) |
| P0-005-010-001-001-001 | �������propsemits | 2026-06-06T07:06 | ? | basic-input�����ı�����������Vue���(ErpInput)+TypeScript���Ͷ���(ErpInputProps/ErpInputEmits/ErpInputExpose/ValidatorRule)+19�Ԫ����ȫ��ͨ����vue-tsc����� | (���ύ) |
| P0-005-010-001-001-002 | ʵ�����ģ��ṹ | 2026-06-06T07:12 | ? | ��ǿbasic-inputģ��ṹ��BEM����(.basic-input__header/content/footer)+����̬/��̬/����̬��̬�л�+v-for������Ϣ�б�+�¼����η�(.stop)+CSS�����������䣻����loading/maxLength/showWordLimit/size props��36�Ԫ����ȫ��ͨ����vue-tsc����� | (���ύ) |
| P0-005-010-001-001-003 | ʵ������߼� | 2026-06-06T07:22 | ? | �ع�Ϊref+watchģʽ(innerValue);����displayValue/isValid/errorMsg��������;watch(fieldConfig)���³�ʼ��;onMounted/onBeforeUnmount��������;handleInput�¼�����;FieldConfig����title����;36�����ͨ��;vue-tsc��basic-input���� | (���ύ) |
| P0-005-010-001-002-001-001 | ����������������JSON | 2026-06-06T07:30 | ? | LinkageConditionConfig/LinkageRuleConfig���Ͷ���;buildConditionFn(12�������);parseLinkageJson(JSON��FieldLinkageRule[]);Ĭ����������JSON�ļ�;����˵���ĵ�;48����ͨ��;vue-tsc����� | (���ύ) |
| P0-005-010-001-002-001-002 | ���������ֶα仯 | 2026-06-06T07:45 | ? | useFormLinkage����watchFieldLinkages(�Զ����������ֶ�+�Ž�������+stop����);basic-input������������(watch innerValue��emit linkage event);types����ErpInputLinkageEvent/change emit;99����ͨ��;vite build�ɹ� | (���ύ) |
| P0-005-010-001-002-001-003 | ִ���������� | 2026-06-06T07:50 | ? | ����setRequired��������(types/list-table.d.ts);FieldLinkageState.required;processLinkages����setRequired;applyLinkageResultӦ��requiredChanges;isFieldRequired��ѯ����;reset����;Ĭ�Ϲ���JSONʾ��;106����ͨ��;vue-tsc����� | (���ύ) |
| P0-005-010-001-002-002 | ʵ������ִ������ | 2026-06-06T08:10 | ? | useFormLinkage����ִ����������ʵ��:processLinkages(7�ֶ���)/executeLinkages(ȫ����)/runLinkageChain(����+ѭ�����)/watchFieldLinkages(�Զ�����)/applyLinkageResult+applyOptionsResult(���Ӧ��)/state��ѯ(isFieldVisible/Disabled/Required);������96.13%(>80%);106��������ͨ��;vue-tsc����� | 51696454 |
| P0-005-010-002-001-001 | �������propsemits | 2026-06-06T08:15 | ? | ErpTextarea�����ı���������:Vue���(210��)+TypeScript����(ErpTextareaProps/Emits/Expose��չ��basic-input.d.ts)+40�Ԫ����ȫ��ͨ��;vue-tsc����� | 18ef7fbd |
| P0-005-010-002-001-002 | ʵ�����ģ��ṹ | 2026-06-06T08:20 | ? | ErpTextareaģ��ṹ:BEM����(basic-input--textarea/__header/__content/__footer)+������Ⱦ(v-if:loading/header/footer/errors)+�б���Ⱦ(v-for errorMessages :key)+Element Plus(el-input textarea/el-skeleton)+�¼����η�(.stop)+Scoped��ʽ;40����ͨ��;vue-tsc����� | c8496710 |
| P0-005-010-002-001-003 | ʵ������߼� | 2026-06-06T08:20 | ? | ErpTextarea����߼�:ref��Ӧʽ״̬(innerValue/errorMessages/lastEmittedValue)+computed(displayValue/isValid/errorMsg)+watch(modelValue/fieldConfig/linkage)+�¼�����(handleInput/handleFocus/handleBlur)+validate(ͬ��+�첽У����)+reset+onMounted/onBeforeUnmount��������;40����ȫ��ͨ��;vue-tsc����� | fcc89084 |
| P0-005-010-002-002-001-001 | �����ֶ�����API��ȡ��ǰ���ݵ��ֶ������б� | 2026-06-06T08:45 | ? | fieldConfig APIģ��+���Ͷ���:src/api/types/fieldConfig.ts(FieldConfigItem/FieldConfigQuery/FieldConfigListResponse)+src/api/modules/fieldConfig.ts(getFieldConfigList/getFieldConfigItem);vue-tsc�����;JSDoc�ĵ�����˵�� | 7fde4eee |
| P0-005-010-002-002-001-002 | ��̬��Ⱦ�ֶ���� | 2026-06-06T09:00 | ? | ErpFieldRenderer��̬��Ⱦ��:Vue���(133��)+TypeScript����(ErpFieldRendererProps/Emits/Expose/FieldTypeComponentMapping)+36�Ԫ����ȫ��ͨ��;vue-tsc�����;������80.64% | daf4feb3 |
| P0-005-010-002-002-001-003 | ִ���ֶ�У����� | 2026-06-06T09:35 | ? | ��֤ErpFieldRenderer+ErpTextarea�ֶ�У�����:76/76��Ԫ����ȫ��ͨ��(ErpFieldRenderer 36+ErpTextarea 40);vue-tsc�����;6����֤ȫͨ��(autoSize/maxRow/maxLength/v-model/disabled/У��) | e0d59e29 |
| P0-005-010-002-002-001-004 | ִ���ֶ��������� | 2026-06-06T09:50 | ? | ErpFieldRenderer����visible prop(v-if������Ⱦ/validate+reset��������̬)+���Ͷ������(ErpFieldRendererProps.visible)+6���²���(41/41ͨ��);1160/1160ȫ��������ع�;vue-tsc����� | ea6e3aac |
| P0-005-010-002-002-002-001 | �ռ������ֶ�ֵ | 2026-06-06T09:00 | ? | �ֶ�ֵ�ռ���:IFieldCollector�ӿ�+useFieldCollector���ʽ����(27����)+FieldConfigBinder(����ת��)+FormValidator(������У�����);types/basic-input.d.ts����;vue-tsc����� | 67bf65a9 |
| P0-005-010-002-002-002-002 | ִ���ֶ�У����� | 2026-06-06T09:06 | ? | ��֤�ֶ�У�����:153����Ԫ����ȫ��ͨ��(ErpInput 50+ErpTextarea 31+ErpFieldRenderer 36+useFieldCollector 27+v-model����9);��֤�嵥6��ȫ��ͨ��(autoSize/maxRow/maxLength/v-model/У������/disabled̬);���Ա����Ѳ��� | (��֤����) |
| P0-005-010-002-002-002-003 | ���������ֶ�ֵ | 2026-06-06T09:15 | ? | useMutation���ʽ����(create/update/autoDetectMode/version�ص�)+fieldConfig API save endpoints(createFieldConfig/updateFieldConfig)+23����ȫͨ��+1210ȫ���ع���ʧ��;���Ա����Ѳ��� | dbcbb9bd |
| P0-005-010-002-002-002-004 | ������չ�ֶ�ֵ | 2026-06-06T09:30 | ? | useExtFieldSave���ʽ����(extractExtensionFields������չ�ֶ�+saveExtFields����/�����Զ��ж�)+fieldConfig API��չ�˵�(saveExtensionFields/updateExtensionFields)+FieldConfig����isExtension���+23����ȫͨ��;vue-tsc�����;���Ա����Ѳ��� | dfedf04b |
| P0-005-010-002-002-003 | ��֤�ֶ����ü��� | 2026-06-06T09:35 | ? | ��֤�ֶ����ü���:46����ȫͨ��(useMutation23+useExtFieldSave23);ȫ��1233�ع�ͨ��;vue-tsc�����;��Ӳ����������Ϣ;���͡�API��Composable�����������·����;��֤�����Ѳ��� | (��֤����) |
| P0-005-010-003-001-001 | �������propsemits | 2026-06-06T09:45 | ? | ErpNumberInput����¼������:Vue���(175��)+TypeScript����(ErpNumberInputProps/Emits/Expose types/basic-input.d.ts)+29�Ԫ����ȫ��ͨ��(182ȫ���ع�)+ErpFieldRenderer.numberӳ��+FieldTypeComponentMapping��չ;vue-tsc����� | (���ύ)
| P0-005-010-003-001-002 | ʵ�����ģ��ṹ | 2026-06-06T10:00 | ? | ErpNumberInputģ����ǿ:��̬��Ⱦ(���عǼ���/����el-input-number)/BEM�����ռ�(basic-input--number)/v-if+v-for+�¼����η�(.stop)/Scoped��ʽ(CSS������������/5��״̬����/6��BEMԪ��/Element Plus���ѡ����);29����Ԫ����ȫ��ͨ��;182ȫ���ع�ͨ��;vue-tsc����� | (���ύ)


### ģ�����: P0-005 ?

### P0-006 - ��֯�ܹ�ģ�鿪��

| ������ | �������� | ���ʱ�� | ״̬ | ժҪ | SHA |
|---------|---------|---------|:---:|------|-----|
| P0-006-001-003-001-002 | ��дServiceImplʵ���� | 2026-06-07T23:41 | ? | OrgPositionServiceImpl: page/getById/create/update/delete 5����;create(���Ŵ�����У��+ͬ���Ÿ�λ����Ψһ��У��+positionCode����);update(����Ψһ��У���ų�����+�ֹ���);delete(Ա�����ü��hrm_employee.position_id+�߼�ɾ��);���״���Entity/DTO/VO/Mapper/Service�ӿ� | a201eee1 |
| P0-006-001-003-001-003 | ��֤Service | 2026-06-08T00:10 | ? | OrgPositionServiceImplTest(21����������ȫ��ͨ��):CreateTests 5��(����+deptNotFound+duplicateName+differentDept+blankName)+UpdateTests 4��(success+duplicateNameExcludeSelf+changeDept+notFound)+DeleteTests 3��(success+hasEmployee+notFound)+PageTests 4��(deptFilter+keywordSearch+combinedFilter+noFilter)+GetByIdTests 2��(success+notFound)+TransactionalAnnotationTests 3�� | 5e7dff25 |
| P0-006-001-001-001-001 | ��д�ӿڶ���Service�ӿ� | 2026-06-08T00:41 | ? | OrgCompanyService�ӿ�(5����:page/getById/create/update/delete)+CompanyCreateDTO(7�ֶ�+У��ע��)+CompanyUpdateDTO+CompanyQueryDTO+CompanyListVO(9�ֶ�+@JsonFormat)+CompanyDetailVO(����չ�ֶ�)+OrgCompanyʵ�� | (pending) |
| P0-006-001-001-001-002 | ��дServiceImplʵ���� | 2026-06-08T01:05 | ? | OrgCompanyServiceImpl(5CRUD����):page(keywordģ������+enabledɸѡ)+create(����Ψһ��+���ô����ʽ+Ψһ��У��)+update(�ֹ���+�ų�����)+delete(�������ż��+�߼�ɾ��)+@OperLog��¼ | (pending) |
| P0-006-001-001-001-003 | ��֤Service | 2026-06-08T01:12 | ? | OrgCompanyServiceImplTest(20��������):create(5)/update(3)/delete(3)/page(2)/getById(2)/����ع�(1)/@Transactionalע����֤(4)+5��Salary׮�ļ� | f911aa50 |
| P0-006-001-002-001-003 | ��֤Service | 2026-06-08T01:58 | ? | OrgDepartmentServiceImplTest(22��������ȫ��ͨ��):create(5��companyNotFound/duplicateName/diffParent/topLevel)+update(3��circularRef_self+circularRef_descendant)+delete(4��childDept/position/employee)+tree(2��multiLevel+empty)+page(1)+getById(2)+@Transactional(5) | 10fbe0bc |

| (pending) �� 1a5156f8

### P0-007 - ��Ʒ����ģ�鿪��

| ������ | �������� | ���ʱ�� | ״̬ | ժҪ | SHA |
|---------|---------|---------|:---:|------|-----|
| P0-007-001-003-001-002 | ��дServiceImplʵ���� | 2026-06-08T02:32 | ? | ProductUnitServiceImpl(5CRUD����):list(productId/unitIdɸѡ+����)/getById(������У��)+save(ת������>0+ͬ��Ʒ��λΨһ+������λΨһ)/update(�ų�����У��)+delete(������У��)+ProductUnitMapper����;toVOӳ��creatorId��createBy | (pending)|

### P0-013 - ������DevOps����

| ������ | �������� | ���ʱ�� | ״̬ | ժҪ | SHA |
|---------|---------|---------|:---:|------|-----|
| P0-013-001-001-001 | ��д���Dockerfile | 2026-06-07T23:32 | ? | ��׶�Dockerfile(maven:3.9-eclipse-temurin-17������eclipse-temurin:17-jre-alpine����)+JAR�ֲ�+��root+HEALTHCHECK+.dockerignore | a7f6c21b |
| P0-013-001-001-002 | ��дǰ��Dockerfile | 2026-06-06T10:50 | ? | ǰ�˶�׶�Dockerfile(node:20-alpine pnpm��nginx:1.27-alpine)+nginx.conf(gzip+SPA+��ȫͷCSP/HSTS+�������+healthz)+��root+HEALTHCHECK | 0d0150d4 |
| P0-013-001-001-002 | ��дǰ��Dockerfile(����) | 2026-06-08T02:00 | ? | ����erp-ai-web/Dockerfile+nginx.conf+����vite-plugin-compression | f046086e |
| P0-013-001-002-001 | ��дdocker-compose������� | 2026-06-06T11:05 | ? | docker-compose.yml(7����+����erp_network+5������)+ȫ���񽡿����+depends_on service_healthy+.env.exampleģ�� | e8c441ea |
| P0-013-001-002-001 | ��дdocker-compose�������(�ؽ�) | 2026-06-08T22:30 | ? | �޸�ǰ�˶˿�3000:80+�������URL+nginx ENV����HTTPS�˿�+docker-compose.override.yml+docker-compose.prod.yml | b619612c |
| P0-013-002-002 | �໷��Nginx���� | 2026-06-06T20:25 | ? | nginx.dev.conf(HTTP/CORS */����־)+nginx.staging.conf(HTTP-HTTPS/SSL/����CORS)+nginx.prod.conf(SSL/����/CSP/HSTS/OCSP)+ssl/README.md+.gitignore���� | 557f251b |
| P0-013-001-003 | .env���������ļ� | 2026-06-08T02:10 | ? | .env.example(����������docker-compose:POSTGRES_USER/POSTGRES_DB/MINIO_ROOT_USER/MINIO_ROOT_PASSWORD)+.env.dev/.env.staging/.env.prod(3�׻������컯����)+docs/env-variables.md(����˵���ĵ�����) | 057764c0
| P0-013-001-004 | .dockerignore�ļ���д | 2026-06-08T01:42 | ? | ����.dockerignore(�汾����/IDE/��������/�ĵ�/��������/��־/����/Docker/CI/CD/����/AI����/OS��12���ų�����)+LF����+.env.example������ | (pending)
| P0-013-002-001 | nginx.conf������ | 2026-06-08T02:15 | ? | nginx.conf(ȫ������+gzip/��ȫͷ/upstream/server include)+conf.d/upstream.conf(backend:8080+frontend:80)+conf.d/erp.conf(�������/API����/WebSocket/��̬��Դ+HTML no-cache)+conf.d/gzip.conf(����6)+conf.d/security-headers.conf(7��ȫͷ) | 60ea2eb6
| P0-013-001-002-002 | ��֤docker-compose���� | 2026-06-08T01:49 | ? | scripts/verify-compose.sh(9���Զ�����֤:YAML/����/����/�������/ͨ��/�־û�/�˵���/���ϻָ�/��־)+docs/verification/docker-compose-report.md(��̬���+�����嵥) | (pending)

### P0-014 - ���Ի���ģ��

| ������ | �������� | ���ʱ�� | ״̬ | ժҪ | SHA |
|---------|---------|---------|:---:|------|-----|
| P0-014-003-001 | ���������淶 | 2026-06-06T20:15 | ? | docs/TESTING_GUIDE.md(���Կ�ܰ汾/�����淶/GivenWhenThen����ʽ/Ŀ¼�ṹ/�������Ž�/Mock����)+docs/TEST_NAMING_CONVENTION.md(��������������淶/ǰ���ļ���it�淶/����ʽʾ��/��������TEST_ǰ׺/����嵥) | (���ύ) |
| P0-014-002-003 | Composable����ģ�� | 2026-06-07T17:49 | ? | ComposableTestTemplate.spec.ts(6�ֲ���ģʽ:ֱ�ӵ���/withSetup/�첽����/Mock/�߽�����/��Ӧʽ���)+withSetup���ߺ���+flushPromises+vi.mockģʽ+Pinia/Router mock+fakeTimers | 16b03ee2 |
| P0-014-002-004 | API Mock���� | 2026-06-07T20:00 | ? | MSW 2.14.6��װ+src/mocks/server.ts(setupServer)+src/mocks/handlers.ts(����/��/401/403/500/Loading)+test-setup.ts(beforeAll/afterEach/afterAll/onUnhandledRequest:error) | 4a5410cc |
| P0-014-001-001 | JUnit 5 Mockito������������ | 2026-06-08T01:55 | ? | pom.xml����testcontainers 1.19.7(testcontainers/postgresql/junit-jupiter)+jacoco-maven-plugin 0.8.11+spring-boot-starter-test(JUnit5.11.4/Mockito5.14.2/AssertJ3.26.3)+H2, application-test.yml�Ѵ���, 1118����ִ��ͨ�� | 7136118d |
| P0-014-002-001 | Vitest������������ | 2026-06-08T02:20 | ? | package.json����happy-dom 14.12.3+msw 2.14.6, vitest.config.ts���»���Ϊhappy-dom+coverage����(provider:v8/thresholds:lines=70/branches=60), test-setup.ts�Ѵ���, 40�����ļ�1262����ͨ�� | 7441fb4a |



### ģ�����: P0-014 ?

### P0-011 - �����������ģ�鿪��

| ������ | �������� | ���ʱ�� | ״̬ | ժҪ | SHA |
|---------|---------|---------|:---:|------|-----|
| P0-011-001-001-001-001 | ��д�ӿڶ���Service�ӿ� | 2026-06-07T17:45 | ? | ICurrencyRateService�ӿ�(extends IServiceX<CurrencyRateEntity>)+CRUD����(create/update/delete/getById/pageList)+@Valid+@Transactional+JavaDoc���� | 1f792e1d |
| P0-011-001-002-001-001 | ��д�ӿڶ���Service�ӿ� | 2026-06-07T18:10 | ? | IBankAccountService�ӿ�(extends IServiceX<BankAccountEntity>)+CRUD����(create/update/delete/getById/pageList)+@Valid+@Transactional+JavaDoc���� + BankAccountEntity/CreateDTO/UpdateDTO/QueryDTO/VO | 52fc201f |
| P0-011-001-002-001-003 | ��֤Service | 2026-06-07T23:50 | ? | BankAccountServiceTest(40 test cases: CRUD/create/update/delete/getById/pageList+״̬��ת+Ψһ��У��+@Transactionalע����֤+�߽�����)+Mockito+JUnit5+Spy + ���Ա��� | 388aaa86 |
| P0-011-001-003-001-001 | ��д�ӿڶ���Service�ӿ� | 2026-06-07T21:32 | ? | IAccountService�ӿ�(extends IServiceX<AccountEntity>)+CRUD����(create/update/delete/getById/pageList)+@Valid+@Transactional+JavaDoc���� | (pending) |
| P0-011-001-003-001-002 | ��дServiceImplʵ���� | 2026-06-07T22:05 | ? | AccountServiceImpl(extends ServiceImpl<AccountMapper, AccountEntity>)+CRUD+Ψһ��У��+@Transactional+OperLog + AccountMapper | (pending) |
| P0-011-001-003-001-003 | ��֤Service | 2026-06-07T22:08 | ? | AccountServiceTest(39����������������CRUD/����Ψһ��/��ҳ��ѯ/�߽�����/@Transactionalע��)+AccountService-test-report.md | (pending) |
| P0-011-001-004-001-001 | ��д�ӿڶ���Service�ӿ� | 2026-06-07T22:05 | ? | IVoucherWordService�ӿ�(extends IServiceX<VoucherWordEntity>)+CRUD����(create/update/delete/getById/pageList)+@Valid+@Transactional+JavaDoc���� | 65db290e |
| P0-011-001-005-001-001 | ��д�ӿڶ���Service�ӿ� | 2026-06-07T22:20 | ? | IAccountingPeriodService�ӿ�(extends IServiceX<AccountingPeriodEntity>)+CRUD����+@Valid+@Transactional+JavaDoc���� + AccountingPeriodEntity/CreateDTO/UpdateDTO/QueryDTO/VO | 22c35e9c |
| P0-011-002-004-001-001 | ��д���Ĵ��� | 2026-06-07T23:15 | ? | �����˻�P04��һ�б�ҳ(index.vue)+APIģ��(finance-bankaccount.ts)+VxeTable�������+ͳ�ƿ�Ƭ+��������+����/�༭����+״̬�л�+ɾ��ȷ��+·��ע��/finance/bankaccount | 26c88b0f |
| P0-011-002-006-001-001 | ��д���Ĵ��� | 2026-06-07T23:35 | ? | ��ƿ�ĿP05�����б�ҳ(index.vue)+APIģ��(finance-account.ts)+el-tree����+VxeTable����+ͳ�ƿ�Ƭ+��������+CRUD����+״̬�л�+ɾ��ȷ�� | bdaac116 |
| P0-011-002-007-001-001 | ��д���Ĵ��� | 2026-06-08T00:36 | ? | ��ƿ�ĿP07��һ����ҳ�Ż�:�ϼ���Ŀ����ѡ����(el-tree-select)+��Ŀ�����Զ�����ֻ��+�������㿪��+�����ַ���˫��ת��+����У���Ż�+�ύloading���ظ� | c57f5867 |
| P0-011-002-004-001-002 | ��֤���� | 2026-06-07T23:55 | ? | �����˻��б�ҳ��֤����+�����嵥(6��:ȱController/ȱ������/����Ӳ����/ȱȨ��ָ��/ȱ���ʻ�/ͳ��ƫ��)+vue-tscͨ��+mvn����ͨ�� | b9267a14 |
| P0-011-002-003-001-001 | ��д���Ĵ��� | 2026-06-08T00:55 | ? | ���ֻ���P07��һ����ҳ(index.vue)+APIģ��(finance-currencyrate.ts)+el-dialog+el-form+����/�༭˫ģʽ+�ֶ�(���ֱ���/����/��׼����/����/����/����)+�첽Ψһ��У��+����Լ��+����+submitLoading+Promise<void>�����޸� | (pending) |


### P0-010 - �ֿ����λ��������

| ������ | �������� | ���ʱ�� | ״̬ | ժҪ | SHA |
|---------|---------|---------|:---:|------|-----|
| P0-010-001-001-001-001 | ��д�ӿڶ���Service�ӿ� | 2026-06-07T18:10 | ? | IWarehouseService�ӿ�(extends IServiceX<WarehouseEntity>)+CRUD����(create/update/delete/getById/pageList)+@Valid+@Transactional+JavaDoc���� + WarehouseEntity/CreateDTO/UpdateDTO/QueryDTO/VO | 2b118054 |
| P0-010-002-000-003-001 | ����̨ȫ�������� | 2026-06-07T21:42 | ? | ����̨��ҳ��index.vue(KPI+ͼ��+������������)+Promise.allSettled+������loading+ʱ��ɸѡ����+�쳣����+Symbol������+types.ts | 0c9cc54c |
| P0-010-002-003-001-001 | ��д���Ĵ��� | 2026-06-07T21:55 | ? | ��λ����P04��һ�б�ҳ(index.vue)+APIģ��(location.ts)+���Ͷ���(location.ts) |
| P0-010-002-002-001-001 | ��д���Ĵ��� | 2026-06-07T21:55 | ? | �ֿ��б�ҳ����P07��������(el-dialog+el-form)+����/�༭˫ģʽ+����У��(���Ʊ���/���ͱ�ѡ/�ֻ��Ÿ�ʽ/��ַ����)+createWarehouse/updateWarehouse/getWarehouseDetail API����+״̬�л�+submitLoading���ظ� | acb2ca85 |
| P0-010-002-002-001-001 | ��д���Ĵ��루��ִ��-��Աѡ������ | 2026-06-08T06:10 | ? | �������ֶδӴ��ı������ΪԶ��������Աѡ����(el-select+remote+filterable)+getUserPageList API����+�༭ģʽԤ������ѡ�û�+�����ر����ѡ�� | (pending) |
| P0-010-002-004-001-001 | ��д���Ĵ��� | 2026-06-07T22:05 | ? | ��λ����P07��һ����ҳ(el-dialog+el-form)+����/�༭˫ģʽ+��λ����(�洢/���/�ݴ�/����Ʒ)+createLocation/updateLocation/getLocationDetail API����+����У��+submitLoading���ظ� | (pending) |
| P0-010-002-004-001-002 | ��֤���� | 2026-06-07T22:07 | ? | ��дǰ����֤����(10����֤+6��߽����+������)+�����嵥(2CRITICAL Controllerȱʧ+2MINOR ����/��ʾ) | 197c2a3f |
| P0-010-002-001-001-002 | ��֤���� | 2026-06-08T05:20 | ? | ǰ�˴������41��ȫ��ͨ��(vue-tsc�����/·����ȷ/����У������/�쳣��������/�߽糡���ݴ�)+�����嵥3��(Controllerȱʧ/�˵�ȱʧ/��������) | (pending) |
| P0-010-001-000-001-001 | ��д����̨�ۺ�SQL | 2026-06-07T22:29 | ? | WorkbenchAggregateMapper.java+XML(KPIͳ��selectKpiStats+�ֿ�����selectWarehouseTrendByDay+��λ����selectLocationTrendByDay)+@Mapper+@Param+tenant_id���⻧����+PostgreSQL DATE_TRUNC | 26d0994b |
| P0-010-001-000-001-002 | ��дServiceImplʵ���� | 2026-06-07T22:37 | ? | �޸�WorkbenchAggregateServiceImpl����(WorkbenchAggregateAggregateMapper��inventory��warehouseģ��)+���²���(16����ȫͨ��)+����ͨ�� | (pending) |
| P0-010-002-000-002-001 | EChartsͼ��������� | 2026-06-08T05:10 | ? | ChartArea.vue(��������/��״�Ա�/��ͼ�ֲ�+ResizeObserver+showLoading+ʱ�䷶Χ��/��/���л�)+warehouse-workbench.tsͼ��API���� | (pending) |
| P0-010-002-001-001-001 | ��д���Ĵ��� | 2026-06-08T05:00 | ? | �ֿⶨ��P04��һ�б�ҳ(��������+ͳ�ƿ�Ƭ+VxeTable�������+����300ms+����/�༭����/����ͣ��/ɾ��ȷ��)+API�˵����㼶�淶+·��ע��/warehouse/warehouse | 71344c97 |


### ģ�����: P0-007 ?

| P0-007-001-001-001-001 | ��д�ӿڶ���Service�ӿ� | 2026-06-07T23:30 | ? | ProductClassService�ӿ�(extends IServiceX<ProductClass>)+CRUD(list/getById/save/update/delete)+@Valid+@Transactional + ProductClassDTO/ProductClassQueryDTO/ProductClassVO + ProductClassʵ�� | 9c6e2343 |
| P0-007-001-001-001-002 | ��дServiceImplʵ���� | 2026-06-08T01:33 | ? | ProductClassServiceImpl(extends ServiceImpl<ProductClassMapper,ProductClass>)+CRUD+ҵ��У��(ͬ������Ψһ/parentId����/�ӷ�����/�����0~9999)+@Transactional+BusinessException + ProductClassMapper | 4b922993 |
| P0-007-001-002-001-001 | ��д�ӿڶ���Service�ӿ� | 2026-06-08T02:13 | ? | ProductService�ӿ�(extends IServiceX<Product>)+CRUD(list/getById/save/update/delete)+@Valid+@Transactional + ProductDTO/ProductQueryDTO/ProductVO + Productʵ�� | 6858ad4e |
| P0-007-001-002-001-002 | ��дServiceImplʵ���� | 2026-06-08T02:20 | ? | ProductServiceImpl(extends ServiceImpl<ProductMapper,Product>)+CRUD+ҵ��У��(����Ψһ/��������/���״̬�ݸ������ˡ�����ˡ��Ѳ�����ת)+@Transactional+BusinessException + ProductMapper | 639034e6 |


### ģ�����: P0-013 ?


### ģ�����: P0-006 ?

---

## P0-008 CRM�ͻ�����ģ�鿪��

| ������ | �������� | ���ʱ�� | ״̬ | ժҪ | Git SHA |
|---------|---------|---------|:---:|------|---------|
| P0-008-001-001-001-003 | ��֤Service | 2026-06-08 02:44 | ? | CustomerClassService��Ԫ����43��������CRUD/�߽�/�쳣/���� | 7d2bd799 |


### P0-008 - CRM�ͻ�����ģ�鿪��

| ������ | �������� | ���ʱ�� | ״̬ | ժҪ | SHA |
|---------|---------|---------|:---:|------|-----|
| P0-008-001-001-001-001 | ��д�ӿڶ���Service�ӿ� | 2026-06-07T18:15 | ? | CustomerClassService�ӿ�(extends IServiceX<CustomerClass>)+CRUD����(list/getById/save/update/delete)+@Valid+@Transactional+JavaDoc���� + CustomerClassʵ��/CustomerClassDTO/CustomerClassQueryDTO/CustomerClassVO | 34a7e18a |
| P0-008-001-002-001-001 | ��д�ӿڶ���Service�ӿ� | 2026-06-07T13:15 | ? | TagDefinitionService�ӿ�(extends IServiceX<TagDefinitionEntity>)+CRUD����(list/getById/save/update/delete)+@Valid+@Transactional+JavaDoc���� + TagDefinitionEntity/TagDefinitionDTO/TagDefinitionQueryDTO/TagDefinitionVO | (pending) |
| P0-008-001-001-001-002 | ��дServiceImplʵ���� | 2026-06-07T19:22 | ? | CustomerClassServiceImpl(extends ServiceImpl<CustomerClassMapper,CustomerClass>)+CRUD(list/getById/save/update/delete)+У��(ͬ������Ψһ��+parentId���ü��+�ӷ�����)+BusinessException+@Transactional + CustomerClassMapper | (pending) |
| P0-008-001-002-001-002 | ��дServiceImplʵ���� | 2026-06-08T03:25 | ? | TagDefinitionServiceImpl(extends ServiceImpl<TagDefinitionMapper,TagDefinitionEntity>)+CRUD(list/getById/save/update/delete)+У��(��ǩ����ȫ��Ψһ+��ɫ��ʽ#RRGGBB+ɾ��ǰ�������ü��)+BusinessException+@Transactional+@OperLog + TagDefinitionMapper | 6717d775 |
| P0-008-001-002-001-003 | ��֤Service | 2026-06-07T19:37 | ? | TagDefinitionServiceTest(18 test cases: CRUD/list/getById/save/update/delete+�߽�����+�쳣����)+Mockito+JUnit5 + TagDefinitionService�ӿ�+TagDefinitionEntity/TagDefinitionDTO/TagDefinitionQueryDTO/TagDefinitionVO+test_data.sql | (pending) |
| P0-008-001-003-001-001 | ��д�ӿڶ���Service�ӿ� | 2026-06-07T19:37 | ? | CustomerService�ӿ�(extends IServiceX<Customer>)+CRUD����(list/getById/save/update/delete)+@Valid+@Transactional + Customerʵ��/CustomerDTO/CustomerQueryDTO/CustomerVO | 020e821f |
| P0-008-001-003-001-001 | ��д�ӿڶ���Service�ӿ� | 2026-06-08T00:33 | ? | [����] CustomerService�ӿ�(extends IServiceX<Customer>)+CRUD����(list/getById/save/update/delete)+@Valid+@Transactional + Customerʵ��/CustomerDTO/CustomerQueryDTO/CustomerVO | (pending) |
| P0-008-001-001-001-003 | ��֤Service | 2026-06-07T19:41 | ? | CustomerClassServiceTest(24 test cases: CRUD/list/getById/save/update/delete+�߽�����+����Ψһ��+�ϼ�У��+@Transactionalע����֤)+Mockito+JUnit5+Spy + test_data.sql | (pending) |
| P0-008-001-003-001-002 | ��дServiceImplʵ���� | 2026-06-07T19:52 | ? | CustomerServiceImpl(extends ServiceImpl<CustomerMapper,Customer>)+CRUD(list/getById/save/update/delete)+У��(�ͻ�����ͬ��˾Ψһ+�����ʽ+���״̬��ת)+BusinessException+@Transactional + CustomerMapper | b5ee0799 |
| P0-008-001-003-001-003 | ��֤Service | 2026-06-07T19:49 | ? | CustomerServiceTest(38 test cases: CRUD/list/getById/save/update/delete+�߽�����+���״̬��ת+����Ψһ��+�����ʽ+����ע����֤)+Mockito+JUnit5+Spy | 3f48e9db |
| P0-008-001-004-001-001 | ��д���Ĵ��� | 2026-06-07T20:00 | ? | CustomerContactService�ӿ�+CustomerContactServiceImpl(extends ServiceImpl<CustomerContactMapper,CustomerContact>)+CRUD(list/getById/save/update/delete)+BusinessException+@Transactional + CustomerContactʵ��/Mapper/DTO/QueryDTO/VO | e99215cb |
| P0-008-001-004-001-002 | ��֤���� | 2026-06-07T20:25 | ? | ��֤CustomerContactService+ServiceImpl����ͨ����CRUD������@Transactional��ȷ���쳣�����淶������淶�Ϲ� | 614e16b6 |
| P0-008-001-005-001-001 | ��д�ӿڶ���Service�ӿ� | 2026-06-07T20:55 | ? | CustomerAddressService�ӿ�(extends IServiceX<CustomerAddress>)+CRUD����(list/getById/save/update/delete)+@Valid+@Transactional + CustomerAddressʵ��/CustomerAddressDTO/CustomerAddressQueryDTO/CustomerAddressVO | 6c861b71 |
| P0-008-001-005-001-002 | ��дServiceImplʵ���� | 2026-06-08T17:45 | ? | CustomerAddressServiceImpl(extends ServiceImpl<CustomerAddressMapper,CustomerAddress>)+CRUD(list/getById/save/update/delete)+��ҳɸѡ(customerId/addressType/contactName)+BusinessException+@Transactional + CustomerAddressMapper | f691d2cd |
| P0-008-001-005-001-003 | ��֤Service | 2026-06-08T19:19 | ? | CustomerAddressServiceTest(20 test cases: CRUD/list/getById/save/update/delete+�쳣����+����ע����֤+VOת��)+Mockito+JUnit5+Spy + customeraddress_test_data.sql | f9fd278f |
| P0-008-001-006-001-001 | ��д���Ĵ��� | 2026-06-08T19:30 | ? | CustomerTagRelService�ӿ�+CustomerTagRelServiceImpl(extends ServiceImplX<CustomerTagRelMapper,CustomerTagRel>)+CRUD(list/getById/save/delete)+saveBatch(��ɾ����)+У��(ͬ�ͻ���ǩΨһ��)+BusinessException+@Transactional+@OperLog + CustomerTagRelʵ��/Mapper/DTO/QueryDTO/VO | (pending) |
| P0-008-001-006-001-002 | ��֤���� | 2026-06-08T20:00 | ? | ��֤CustomerTagRelService+ServiceImpl: CRUD����(list/getById/save/saveBatch/delete)+listByCustomerId/listByTagId˫���ѯ+@Transactional��ȷ+@OperLog����+BusinessException�쳣����+Ψһ��У��+����淶�Ϲ�(��Ŀmvn compileʧ����P0-009 SRMģ��ȱ��DTO/Entity/VO���Ǳ�����Χ) | (pending) |
| P0-008-001-003-001-002 | ��дServiceImplʵ���� | 2026-06-08T00:46 | ? | CustomerServiceImpl(extends ServiceImpl<CustomerMapper,Customer>)+CRUD(list/getById/save/update/delete)+У��(�ͻ�����ͬ��˾Ψһ+�����ʽ)+BusinessException+@Transactional+@OperLog + CustomerMapper | (pending) |

| P0-008-001-004-001-001 | ��д���Ĵ��� | 2026-06-08T04:00 | ? | [����] CustomerContactService�ӿ�+CustomerContactServiceImpl(extends ServiceImpl<CustomerContactMapper,CustomerContact>)+CRUD(list/getById/save/update/delete)+У��(ͬ�ͻ���ϵ������Ψһ+Ĭ����ϵ��Ψһ)+BusinessException+@Transactional+@OperLog + CustomerContactʵ��/Mapper/DTO/QueryDTO/VO | (pending) |
| P0-008-001-004-001-002 | ��֤���� | 2026-06-08T04:10 | ? | [����] ��֤�������CustomerContactService+ServiceImpl����ͨ����CRUD����(list/getById/save/update/delete)��@Transactional��ȷ��@OperLog������BusinessException�쳣�����淶����ϵ������Ψһ��У��+Ĭ����ϵ�˹���������淶�Ϲ� | (pending) |

### P0-009 - SRM��Ӧ�̹���ģ�鿪��

| ������ | �������� | ���ʱ�� | ״̬ | ժҪ | SHA |
|---------|---------|---------|:---:|------|-----|
| P0-009-001-001-001-001 | ��д��Ӧ�̷���Service�ӿڶ��� | 2026-06-07T18:20 | ? | SupplierClassService�ӿ�(extends IServiceX<SupplierClass>)+CRUD����(list/getById/save/update/delete)+@Valid+@Transactional+JavaDoc���� + SupplierClassʵ��/SupplierClassDTO/SupplierClassQueryDTO/SupplierClassVO | 235a6637 |



| P0-009-001-001-001-002 | ��дServiceImplʵ���� | 2026-06-07T20:35 | ? | ʵ��SupplierClassServiceImpl����CRUD+ҵ��У�飨����ͬ��Ψһ�ԡ����ڵ�����ԡ��ӷ����飩+������� | 3dc516c4 |
| P0-009-001-002-001-001 | ��дSRM��ǩ����Service�ӿ� | 2026-06-07T20:00 | ? | SrmTagDefinitionService�ӿ�(extends IServiceX<SrmTagDefinition>)+CRUD����(list/getById/save/update/delete)+@Valid+@Transactional + SrmTagDefinitionʵ��/SrmTagDefinitionDTO/SrmTagDefinitionQueryDTO/SrmTagDefinitionVO | ea269fb7 |
| P0-009-001-002-001-002 | ��дSRM��ǩ����ServiceImplʵ���� | 2026-06-07T21:05 | ? | ʵ��SrmTagDefinitionServiceImpl(CRUD+��ǩ����ȫ��ΨһУ��+��ɫ��ʽ#RRGGBBУ��+�������)+SrmTagDefinitionMapper | 7f9d7892 |
| 
| P0-009-001-001-001-003 | ��֤SupplierClassService | 2026-06-07T20:05 | ? | SupplierClassServiceTest(24����������������CRUD��������+�߽�����+����ع���֤+�쳣����+����Ψһ��+�ϼ�������У��) + supplierclass_test_data.sql | 247343a6 |
| P0-009-001-002-001-003 | ��֤SrmTagDefinitionService | 2026-06-07T22:10 | ? | SrmTagDefinitionServiceTest(30����������������CRUD��������+�߽�����+��ɫ��ʽУ��+��ǩ����Ψһ��+����ע����֤) + srmtagdefinition_test_data.sql | f26cf3f0 |
| P0-009-001-003-001-001 | ��д��Ӧ��Service�ӿڶ��� | 2026-06-07T20:07 | ? | SupplierService�ӿ�(extends IServiceX<Supplier>)+CRUD����(list/getById/save/update/delete)+@Valid+@Transactional + Supplierʵ��/SupplierDTO/SupplierQueryDTO/SupplierVO | 8ae5c328 |
| P0-009-001-005-001-001 | ��д��Ӧ�̵�ַService�ӿڶ��� | 2026-06-07T23:00 | ? | SupplierAddressService�ӿ�(extends IServiceX<SupplierAddress>)+CRUD����(list/getById/save/update/delete)+@Valid+@Transactional + SupplierAddressʵ��/SupplierAddressDTO/SupplierAddressQueryDTO/SupplierAddressVO | TBD |
| P0-009-001-003-001-002 | ��дServiceImplʵ���� | 2026-06-07T23:30 | ? | SupplierServiceImpl(CRUD+���ƹ�˾��Ψһ+�����ʽ+���״̬��תУ��+����) + SupplierMapper | 759b2cb8 |
| P0-011-001-002-001-002 | ��дServiceImplʵ���� | 2026-06-07T20:17 | ? | BankAccountServiceImpl.java (CRUD+Ψһ��У��+�������+BusinessException�쳣����) + BankAccountMapper.java | TBD |
| P0-011-001-001-001-002 | ��дServiceImplʵ���� | 2026-06-07T21:16 | ? | CurrencyRateServiceImpl.java (CRUD+���ֱ���Ψһ��У��+��������У��+�������+BusinessException�쳣����) + CurrencyRateMapper.java | b8a3cd7b |
| P0-011-001-001-001-003 | ��֤Service | 2026-06-07T21:30 | ? | CurrencyRateServiceTest (41����������������CRUD/����Ψһ��/����У��/��ҳ��ѯ/�߽�����/@Transactionalע��/����ع�) + CurrencyRateService-test-report.md | 45f428a7 |
| P0-009-001-004-001-001 | ��д��Ӧ����ϵ�˺��Ĵ��� | 2026-06-07T23:45 | ? | SupplierContactȫ��(Entity/DTO/QueryDTO/VO/Mapper/Service/ServiceImpl)+CRUD��������+BusinessException�쳣����+������� | 7c39a05d |
| P0-009-001-004-001-002 | ��֤��Ӧ����ϵ��Service | 2026-06-07T22:25 | ? | SupplierContactService��֤(CRUD����+����ע����ȷ+BusinessException�쳣����+����ͨ��) | TBD |
| P0-009-001-003-001-003 | ��֤SupplierService | 2026-06-07T20:28 | ? | SupplierServiceTest(36����������������CRUD��������+�߽�����+����ע��+�쳣����+����Ψһ��+�����ʽ+���״̬��ת) + supplier_test_data.sql | 2d6a16b0 |
| P0-009-001-003-001-003 | ��ǿSupplierService��Ԫ���� | 2026-06-07T21:13 | ? | ������������(CountDownLatch)+�߽���������6��(null/������/��Ч����)������������27��չ��34����ȫ��ͨ�� | 26a61706 |

### P0-010-001-000-001-001 ��д����̨�ۺ�SQL

| ���� | ֵ |
|------|-----|
| ������ | P0-010-001-000-001-001 |
| ���ʱ�� | 2026-06-07T20:38 |
| ״̬ | ? |
| ժҪ | ��дWorkbenchAggregateMapper�ӿڼ�XML��ʵ��KPI�ۺ�ͳ��(warehouseTotal/warehouseActive/locationTotal/locationActive)�����Ʋ�ѯ(���շ���) |
| ���� | W2 |

### P0-010-001-000-001-002 ��дServiceImplʵ����

| ���� | ֵ |
|------|-----|
| ������ | P0-010-001-000-001-002 |
| ���ʱ�� | 2026-06-07T20:41 |
| ״̬ | ? |
| ժҪ | ��дWorkbenchAggregateService�ӿ�+WorkbenchAggregateServiceImplʵ���࣬��װ����̨KPI�ۺϲ�ѯ�����Ʋ�ѯ������@Cacheable����(TTL=5min)��ʹ��Sa-Token��ȡ�⻧IDʵ�ֶ��⻧���� |
| ���� | W1 |

### P0-010-001-000-001-003 ��֤Service

| ���� | ֵ |
|------|-----|
| ������ | P0-010-001-000-001-003 |
| ���ʱ�� | 2026-06-07T20:50 |
| ״̬ | ? |
| ժҪ | ��дWorkbenchAggregateServiceTest(16����ȫͨ��)������KPIͳ��/�ֿ�����/��λ������������+null����+�⻧ID��ȡ(String/Long/null/�쳣)+����������֤+@Cacheableע����֤ |
| ���� | W3 |
| Git Commit | 888ebbe4 |

### P0-010-001-001-001-002 ��дServiceImplʵ����

| ���� | ֵ |
|------|-----|
| ������ | P0-010-001-001-001-002 |
| ���ʱ�� | 2026-06-07T20:50 |
| ״̬ | ? |
| ժҪ | ����WarehouseMapper(WarehouseEntity��BaseMapperX) + WarehouseServiceImpl(�̳�ServiceImpl��ʵ��IWarehouseService�����ֿ����Ψһ��У��/״̬��תУ��/��ɾ��/@Transactional/OperLogע��) |
| ���� | W2 |
| Git Commit | 02ec5e6f |

### P0-010-001-002-001-001 ��д�ӿڶ���Service�ӿ�

| ���� | ֵ |
|------|-----|
| ������ | P0-010-001-002-001-001 |
| ���ʱ�� | 2026-06-07T21:15 |
| ״̬ | ? |
| ժҪ | ����LocationEntity/LocationCreateDTO/LocationUpdateDTO/LocationQueryDTO/LocationVO/LocationMapper + ILocationService�ӿ�(�̳�IServiceX������CRUD������д������ע@Transactional) |
| ���� | W2 |
| Git Commit | 503e1fdd |

| P0-010-001-001-001-003 | ��֤Service | 2026-06-07T21:05:35 | ? | ��дWarehouseService��Ԫ����31������ȫ��ͨ�� | W3 |

### P0-010-001-002-001-002 ��дServiceImplʵ����

| ���� | ֵ |
|------|-----|
| ������ | P0-010-001-002-001-002 |
| ���ʱ�� | 2026-06-07T21:10:34 |
| ״̬ | ? |
| ժҪ | ��дLocationServiceImpl(�̳�ServiceImpl<LocationMapper,LocationEntity>��ʵ��ILocationService������λ����ֿ���Ψһ��У��/״̬��תУ��/@Transactional/OperLogע��/BusinessException�쳣����/��ҳ��ѯ����) |
| ���� | W4 |
| Git Commit | fc2e247b |

### P0-010-001-002-001-003 ��֤Service

| ���� | ֵ |
|------|-----|
| ������ | P0-010-001-002-001-003 |
| ���ʱ�� | 2026-06-07T21:14:18 |
| ״̬ | ? |
| ժҪ | ��дLocationServiceTest(36����Ԫ��������������CRUD��������/�ֿ���Ψһ��У��/״̬��תУ��/��ɾ��/��ҳ��ѯ������ɸѡ/�߽糡��/toVOת��/����ע����֤��ʹ��JUnit5+Mockito+MockitoExtension) |
| ���� | W3 |
| Git Commit | 1ae36978 |

### P0-010-002-000-001-001 KPI��Ƭ�������

| ���� | ֵ |
|------|-----|
| ������ | P0-010-002-000-001-001 |
| ���ʱ�� | 2026-06-07T21:32:00 |
| ״̬ | ? |
| ժҪ | ��дKpiCardArea���(�ֿ⹤��̨KPI��Ƭ��/4��ָ�꿨Ƭ���ֿ�����+���òֿ�+��λ����+���ÿ�λ/��Ӧʽդ�񲼾�el-row+el-col/����GET /api/warehouse/workbench/kpi/��ȫ����null+NaNֵ)+ warehouse-workbench APIģ�� |
| ���� | W3 |
| Git Commit | d5a3b4dd |

### P0-010-002-001-001-002 ��֤����

| ���� | ֵ |
|------|-----|
| ������ | P0-010-002-001-001-002 |
| ���ʱ�� | 2026-06-07T22:05:00 |
| ״̬ | ? |
| ժҪ | ��֤�ֿⶨ���б�ҳ(���index.vue+warehouse.ts+types����/����pnpm build����ͨ��/��д���Ա��溬8����֤+5��߽����/�����嵥4�·��δע��+API·����һ��+ȱ��������+ͳ�ƽ���ǰҳ/����淶�Ϲ�/�쳣��������) |
| ���� | W3 |
| Git Commit | 92df00d6 |

### P0-010-002-001-001-002 ��֤���ܣ���ִ�У�

| ���� | ֵ |
|------|-----|
| ������ | P0-010-002-001-001-002 |
| ���ʱ�� | 2026-06-08T05:20:00 |
| ״̬ | ? |
| ժҪ | ��ִ����֤��ǰ�˴������41��ȫ��ͨ��(vue-tsc�����/·����ȷ/����У������/�쳣��������/�߽糡���ݴ�)+������(test-report+issues)+�����嵥3��(P0:Controllerȱʧ/P1:�˵�ȱʧ/P2:��������) |
| ���� | W5 |
| Git Commit | (pending) |

| P0-011-001-002-001-002 | ��дServiceImplʵ���� | 2026-06-07 21:22:55 | ? | ��дBankAccountServiceImplʵ���࣬����CRUD+Ψһ��У��+״̬��תУ��+@Transactional | 7b195f18 |

| P0-011-001-003-001-001 | ��д�ӿڶ���Service�ӿ� | 2026-06-07T21:30 | ? | IAccountService�ӿ�(extends IServiceX<AccountEntity>)+CRUD����(create/update/delete/getById/pageList)+@Valid+@Transactional+JavaDoc���� + AccountEntity/AccountCreateDTO/AccountUpdateDTO/AccountQueryDTO/AccountVO | f6a76404 |

| P0-010-002-000-002-001 | echartsͼ��������� | 2026-06-07T22:25 | ? | ����ChartArea.vue(����ͼ����+��״ͼ�Ա�+��ͼ�ֲ�4ͼ2x2����)+��չwarehouse-workbench.ts(ChartTrendItem/ChartDistributionItem/WarehouseWorkbenchChartVO����+getWarehouseWorkbenchChartApi)+ResizeObserver����Ӧ+��/��/��ʱ�䷶Χɸѡ+ECharts showLoading/hideLoading | 1262abf1 |

### P0-010-002-002-001-002 ��֤����

| ���� | ֵ |
|------|-----|
| ������ | P0-010-002-002-001-002 |
| ���ʱ�� | 2026-06-07T22:10:00 |
| ״̬ | ? |
| ժҪ | ��֤�ֿⶨ�����ҳ(���warehouse/index.vue��������/����pnpm build����ͨ�������ʹ���/��дǰ����֤���溬7����֤+5��߽����/��д�����嵥4�ControllerȱʧCRITICAL+API·����һ��CRITICAL+ͳ�ƿ�Ƭ���ݲ�׼ȷMINOR+ȱ��״̬��ʾMINOR) |
| ���� | W3 |
| Git Commit | 968bf95c |

### P0-010-002-003-001-002 ��֤����

| ���� | ֵ |
|------|-----|
| ������ | P0-010-002-003-001-002 |
| ���ʱ�� | 2026-06-07T22:15:00 |
| ״̬ | ? |
| ժҪ | ��֤��λ�����б�ҳ(��̬���location/index.vue��������/ǰ��vue-tsc���������/��д��֤����7����֤4ͨ��2δͨ��1����ͨ��/��д�����嵥4��:ȱLocationController+ȱWarehouseController+ȱ·��+���ʻ�Ӳ����) |
| ���� | W3 |
| Git Commit | 0ca62806 |

### P0-010-002-003-001-002 ������֤��W5��

| ���� | ֵ |
|------|-----|
| ������ | P0-010-002-003-001-002 |
| ���ʱ�� | 2026-06-08T04:31 |
| ״̬ | ?��������֤�� |
| ժҪ | ������֤��λ�����б�ҳ��vue-tsc�����/·�����޸�?/2��Controllerȱʧ������/���ʻ�δ�޸�/������֤����7����֤5ͨ��1����ͨ��1δͨ��/���������嵥1���޸�+3���޸��� |
| ���� | W5 |
| Git Commit | --pending-- |

## P0-011 - �����������ģ�鿪��

### P0-011-001-004-001-002 ��дServiceImplʵ����

| ���� | ֵ |
|------|-----|
| ������ | P0-011-001-004-001-002 |
| ���ʱ�� | 2026-06-07T22:18:00 |
| ״̬ | ? |
| ժҪ | ʵ��VoucherWordServiceImpl(�̳�ServiceImpl/ʵ��CRUD����/create������Ψһ��У��/update��������+Ψһ��+״̬��תУ��/delete��������У��+��ɾ��/pageList��ҳ��ѯ/@Transactional�������/BusinessExceptionͳһ�쳣/����VoucherWordEntity+DTO+VO+Mapper֧����/mvn compileͨ��) |
| ���� | W4 |
| Git Commit | e673e4d5 |

### P0-011-001-004-001-003 ?
| ���� | ֵ |
|------|-----|
| ������ | P0-011-001-004-001-003 |
| �������� | ��֤Service |
| ���ʱ�� | 2026-06-07T22:25 |
| ״̬ | ? |
| ժҪ | ��дVoucherWordService��Ԫ����(38������)������CRUD/Ψһ��У��/״̬��ת/����ע�⣬ȫ��ͨ�� |
| Git SHA | 331547694e756525d86122c87d07e7b18edf0a9d |

### P0-011-002-008-001-002 ?
| ���� | ֵ |
|------|-----|
| ������ | P0-011-002-008-001-002 |
| �������� | ��֤���ܣ�ƾ֤��P04��һ�б�ҳ�� |
| ���ʱ�� | 2026-06-08T00:51 |
| ״̬ | ? |
| ժҪ | ��֤ƾ֤��P04�б�ҳ����(�������ǰ��Vue/APIģ��/·��/���ʻ�/���API����)�����ֲ��޸�VoucherWordControllerȱʧ(����Controller��¶6��REST�˵�)+updateStatus����ȱʧ(����Service����)����дǰ����֤����������嵥 |
| ���� | W5 |
| Git SHA | 2f6327c8 |

### P0-010-001-000-001-001 ��д����̨�ۺ�SQL���ֿ�ģ����ִ�У�

| ���� | ֵ |
|------|-----|
| ������ | P0-010-001-000-001-001 |
| ���ʱ�� | 2026-06-07T22:35 |
| ״̬ | ? |
| ժҪ | �����ֿ�ģ��WorkbenchAggregateAggregateMapper(�ӿ�+XML)��ʵ��KPI�ۺ�ͳ��(warehouseTotal/warehouseActive/locationTotal/locationActive)�����Ʋ�ѯ(���շ���)�������warehouseģ����ȷλ�� |
| ���� | W5 |
| Git SHA | 2fe36492 |

### P0-011-001-005-001-002 ?
| ���� | ֵ |
|------|-----|
| ������ | P0-011-001-005-001-002 |
| �������� | ��дServiceImplʵ���� |
| ���ʱ�� | 2026-06-07T22:40 |
| ״̬ | ? |
| ժҪ | ����AccountingPeriodServiceImpl(�̳�ServiceImpl<AccountingPeriodMapper,AccountingPeriodEntity>/ʵ��IAccountingPeriodService/CRUD/crate���ص�У��/update��������+�ص�У��/delete��������У��+��ɾ��/getById��������У��/pageList��fiscalYear/period/periodStatusɸѡ+����/@Transactional/BusinessException)+AccountingPeriodMapper(BaseMapperX)/mvn compileͨ�� |
| ���� | W4 |
| Git SHA | bb0b97ff

### P0-011-001-005-001-003 ?
| ���� | ֵ |
|------|-----|
| ������ | P0-011-001-005-001-003 |
| �������� | ��֤Service |
| ���ʱ�� | 2026-06-07T22:50 |
| ״̬ | ? |
| ժҪ | ����AccountingPeriodServiceTest(35����������ȫͨ����create6/update5/delete2/getById2/pageList10/toVO1/@Transactional5/�߽�4)�����Ա��������ɣ�mvn compileͨ�� |
| ���� | W3 |
| Git SHA | e42fd55d

### P0-011-002-001-001-001 ?
| ���� | ֵ |
|------|-----|
| ������ | P0-011-002-001-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-07T22:55 |
| ״̬ | ? |
| ժҪ | ����finance-workbench API��(getFinanceWorkbenchApi����/api/finance/workbench)�Ͳ�����̨ҳ�����(index.vue��KPI��Ƭ4ָ��/��Ŀ���ͱ�ͼ/�¶���������ͼ/loading����״̬/Suspense����/ECharts��Ⱦ/resize����)��ע�ᾲ̬·��/finance/workbench��������Ӣ��i18n������vue-tsc���ͼ��ͨ�� |
| ���� | W5 |
| Git SHA | (���ύ)

### P0-011-001-006-001-001 ?
| ���� | ֵ |
|------|-----|
| ������ | P0-011-001-006-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-08T01:58 |
| ״̬ | ? |
| ժҪ | ����FinanceWorkbenchAggregateServiceImpl(@Cacheable����/KPI�ۺ�/��Ŀ���ͷֲ�/�¶�����/���⻧����)+FinanceWorkbenchAggregateVO(�ۺ�����VO/TrendItem�ڲ���)��mvn compileͨ����������֤����(001-006-001-002)��ͨ�� |
| ���� | W4 |
| Git SHA | 10fbe0b

### P0-011-001-006-001-002 ?
| ���� | ֵ |
|------|-----|
| ������ | P0-011-001-006-001-002 |
| �������� | ��֤���� |
| ���ʱ�� | 2026-06-07T22:48 |
| ״̬ | ? |
| ժҪ | ��֤FinanceWorkbenchAggregateServiceImpl��˹��ܣ��������+������֤�������������֤����������嵥��4���0����������mvn compileͨ�� |
| ���� | W4 |
| Git SHA | 821f1a79

### P0-011-002-001-001-001 ?
| ���� | ֵ |
|------|-----|
| ������ | P0-011-002-001-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-07T22:55 |
| ״̬ | ? |
| ժҪ | ����finance-workbench API��(getFinanceWorkbenchApi����/api/finance/workbench)�Ͳ�����̨ҳ�����(index.vue��KPI��Ƭ4ָ��/��Ŀ���ͱ�ͼ/�¶���������ͼ/loading����״̬/ECharts��Ⱦ/resize����)��ע�ᾲ̬·��/finance/workbench��������Ӣ��i18n������vue-tsc���ͼ��ͨ�� |
| ���� | W5 |
| Git SHA | 821f1a79

### P0-011-002-001-001-002 ?
| ���� | ֵ |
|------|-----|
| ������ | P0-011-002-001-001-002 |
| �������� | ��֤���� |
| ���ʱ�� | 2026-06-07T23:00 |
| ״̬ | ? |
| ժҪ | ��֤FinanceWorkbenchǰ��ҳ�棨�������+vue-tsc������֤+i18n���Ǽ�飩������ǰ����֤���棨����9.3/10���������嵥��1 CRITICAL: Controllerȱʧ, 1 MEDIUM: ���ظ�����, 2 LOW����vue-tsc���ͼ��ͨ�� |
| ���� | W6 |
| Git SHA | 89e07c5d

### P0-011-002-002-001-001 ?
| ���� | ֵ |
|------|-----|
| ������ | P0-011-002-002-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-07T23:05 |
| ״̬ | ? |
| ժҪ | ����finance-currencyrate API��(getCurrencyRatePageApi/deleteCurrencyRateApi)�ͱ��ֻ���P04�б�ҳ(index.vue��ͳ�ƿ�Ƭ/��������/VxeTable�������/��ҳ/ɾ��ȷ��)��vue-tsc���ͼ��ͨ�� |
| ���� | W3 |
| Git SHA | 81a80e29

## P0-011-002-001-001-002

- **���ʱ��**��2026-06-07T23:00
- **״̬**��?
- **ժҪ**����֤����������ù���̨ǰ�˹��ܣ����ֲ��޸�ȱʧController�˵����⣬ǰ�˹���ͨ����2�ݲ��Ա����ѽ���
- **Git SHA**��87f74add

### P0-011-002-002-001-002 ?
| ���� | ֵ |
|------|-----|
| ������ | P0-011-002-002-001-002 |
| �������� | ��֤���� |
| ���ʱ�� | 2026-06-07T23:03 |
| ״̬ | ? |
| ժҪ | ��֤���ֻ���P04�б�ҳǰ�˹��ܣ��������ͨ����·��ע��ȱʧ���޸�(FINANCE_CURRENCYRATE)��Controllerȱʧ�ѱ�ǣ�����issues.md(4������1���޸�)������ǰ����֤���� |
| ���� | W6 |
| Git SHA | 6a91a652

### P0-011-002-003-001-001 ?
| ���� | ֵ |
|------|-----|
| ������ | P0-011-002-003-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-07T23:10 |
| ״̬ | ? |
| ժҪ | ʵ�ֱ��ֻ���P07��һ����ҳ������el-dialog��������(6�ֶκ��첽Ψһ��У��)��API������create/update/checkCode�ӿڣ�vue-tsc���ͼ��ͨ����vite buildͨ�� |
| ���� | W4 |
| Git SHA | bb69d9d5

### P0-011-002-003-001-002 ?
| ���� | ֵ |
|------|-----|
| ������ | P0-011-002-003-001-002 |
| �������� | ��֤���� |
| ���ʱ�� | 2026-06-07T23:35 |
| ״̬ | ? |
| ժҪ | ��֤���ֻ���P07��һ����ҳ��vue-tsc����ͨ����vite buildͨ��(6.94s)������ǰ����֤����(30����/27ͨ��)������issues.md(ISS-3���޸���ISS-2/ISS-4������) |
| ���� | W3 |
| Git SHA | (���ύ)

### P0-011-002-010-001-001 ?
| ���� | ֵ |
|------|-----|
| ������ | P0-011-002-010-001-001 |
| �������� | ��дCacheManager Service |
| ���ʱ�� | 2026-06-07T23:45 |
| ״̬ | ? |
| ժҪ | ��д�������CacheManager Service��ICacheManagerService�ӿ�(6������)��CacheManagerServiceImplʵ��(SCAN������ֹKEYS��JSON�����л���TTL��ѯ��ģʽɾ�����1000Key��������־)��CacheKeyVO/CacheStatsVO |
| ���� | W4 |
| Git SHA | 253fe624

### P0-011-002-010-001-002 ?
| ���� | ֵ |
|------|-----|
| ������ | P0-011-002-010-001-002 |
| �������� | ��д�������ControllerȨ�޿��� |
| ���ʱ�� | 2026-06-08T02:30 |
| ״̬ | ? |
| ժҪ | ��дCacheManagerController(5��RESTful�˵�: list/value/stats/deleteByKey/deleteByPattern)+@RequirePermission("system:cache:manage")+RT<T>ͳһ��Ӧ+Swaggerע������ |
| ���� | W5 |
| Git SHA | 6f639e60

### P0-011-002-010-001-003 ?
| ���� | ֵ |
|------|-----|
| ������ | P0-011-002-010-001-003 |
| �������� | ��֤������� |
| ���ʱ�� | 2026-06-08T00:00 |
| ״̬ | ? |
| ժҪ | ���������֤�����������(8��ȫͨ��): SCAN����Key�б�/ģʽ����/Value JSON����/TTLչʾ/��������ɾ��/Ȩ��403/������־, ��д��֤����docs/test-reports/cache-management-test.md |
| ���� | W6 |
| Git SHA | 71ad98c5

| P0-012-001-001-001-001 | ��д�ӿڶ���Service�ӿ� | 2026-06-07T23:50 | ? | ����IEmployeeService�ӿ�(�̳�IServiceX)�����Entity/DTO/VO | (pending-commit) |
| P0-012-001-001-001-002 | ��дServiceImplʵ���� | 2026-06-07T23:40 | ? | ����EmployeeServiceImpl(�̳�ServiceImpl/����CRUD/����Ψһ��У��/����֤����)��EmployeeMapper | (pending-commit) |
| P0-012-001-001-001-003 | ��֤Service | 2026-06-08T00:01 | ? | ��дEmployeeServiceTest(34����������ȫͨ��/JUnit5+Mockito/����CRUD+Ψһ��+����У��+����ع�+�߽糡��+����֤����) | (pending-commit) |
| P0-012-001-002-001-001 | ��д�ӿڶ���Service�ӿ� | 2026-06-08T00:20 | ? | ����IRecruitmentService�ӿ�(�̳�IServiceX<RecruitmentEntity>/5��CRUD����/@Valid���/@Transactionalд����)��RecruitmentEntity/DTO/VO | (pending-commit) |
| P0-012-001-002-001-002 | ��дServiceImplʵ���� | 2026-06-08T00:40 | ? | ����RecruitmentServiceImpl(�̳�ServiceImpl/����CRUD/��ֹ����У��+״̬��תУ��/@Transactional+BusinessException+@OperLog)+RecruitmentMapper | (pending-commit) |
| P0-012-001-003-001-001 | ��д�ӿڶ���Service�ӿ� | 2026-06-08T00:34 | ? | ����IAttendanceService�ӿ�(�̳�IServiceX<AttendanceEntity>/5��CRUD����/@Valid���/@Transactionalд����/����JavaDoc)��AttendanceEntity/DTO/VO | (pending-commit) |
| P0-012-001-003-001-002 | ��дServiceImplʵ���� | 2026-06-08T00:42 | ? | ����AttendanceServiceImpl(�̳�ServiceImpl/����CRUD/Ա��+����Ψһ��У��/@Transactional+BusinessException+@OperLog)+AttendanceMapper | (pending-commit) |
| P0-012-001-003-001-003 | ��֤Service | 2026-06-08T00:51 | ? | ��дAttendanceServiceTest(37����������ȫͨ��/JUnit5+Mockito/����CRUD+Ψһ��+����У��+����ع�+�߽糡��)�����Ա��� | (pending-commit) |

### P0-011-001-002-001-001 ?
| ���� | ֵ |
|------|-----|
| ������ | P0-011-001-002-001-001 |
| �������� | ��д�ӿڶ���Service�ӿ� |
| ���ʱ�� | 2026-06-07T23:35 |
| ״̬ | ? |
| ժҪ | ��֤IBankAccountService�ӿ�(�̳�IServiceX<BankAccountEntity>/CRUD����5��/create+update��@Valid DTO���/delete��@Transactional/getById+pageList������/JavaDoc����/@Transactional��д��������)�������Ѵ����ҷ��Ϲ��mvn clean compileͨ�� |
| ���� | W5 |
| Git SHA | (pending-commit) |

| P0-011-001-002-001-002 | ��дServiceImplʵ���� | 2026-06-07T23:43 | ? | BankAccountServiceImpl(extends ServiceImpl/CRUD����/Ψһ��У��+״̬��תУ��/@Transactional+BusinessException+@OperLog/mvn compileͨ��) | 3a965dac |

## P0-008-001-001-001-001 ��д�ӿڶ���Service�ӿ�
- ���ʱ��: 2026-06-07 23:54
- ״̬: ?
- ժҪ: ����CustomerClassService�ӿڣ�����@Transactional(readOnly=true)��@Validע��
- ģ��: P0-008 CRM�ͻ�����ģ��

## P0-011-002-001-001-001 �� ��д���Ĵ���

- **���ʱ��**��2026-06-07T23:59
- **״̬**��? �����
- **ժҪ**������������ù���̨���Ĵ��루ҳ��+API�㣩��ʵ�֣�����KPI��Ƭ��ECharts����ͼ����Ŀ���ͷֲ���ͼ
- **������**��
  - erp-ai-web/src/views/finance/financeworkbench/index.vue
  - erp-ai-web/src/api/modules/finance-workbench.ts

## P0-011-002-005-001-001 �� ��д���Ĵ���

- **���ʱ��**��2026-06-08T00:15
- **״̬**��? �����
- **ժҪ**�������˻�����ҳ���Ĵ��룬����checkBankAccountNoApi�첽Ψһ��У�麯����bankAccountNo�ֶ������첽Ψһ��У��
- **������**��
  - erp-ai-web/src/views/finance/bankaccount/index.vue�����£�
  - erp-ai-web/src/api/modules/finance-bankaccount.ts�����£�
- **����**��W6
- **Git SHA**��39534344



## P0-011-001-002-001-003 �� ��֤Service

- **���ʱ��**��2026-06-08T00:10
- **״̬**��? �����
- **ժҪ**��BankAccountService��Ԫ������֤ͨ����40����������ȫ��PASS������CRUD��Ψһ��У�顢״̬��ת��@Transactionalע����֤���߽糡��
- **������**��
  - src/test/java/com/erp/module/finance/service/BankAccountServiceTest.java��791�У�40��������
  - docs/test-reports/BankAccountService-test-report.md
- **����**��W5


## P0-011-002-004-001-002 �� ��֤����

- **���ʱ��**��2026-06-08T00:20
- **״̬**��? �����
- **ժҪ**�������˻��б�ҳǰ����֤ͨ����36����֤ȫ��PASS��ǰ��˱���ͨ��������4����/���������Ѽ�¼
- **������**��
  - docs/test-reports/finance-BankAccount-frontend-test.md
  - docs/test-reports/finance-BankAccount-issues.md
- **����**��W6

## P0-011-002-006-001-002 �� ��֤����

- **���ʱ��**��2026-06-08T00:45
- **״̬**��? �����
- **ժҪ**����ƿ�Ŀ�����б�ҳǰ�˴�����֤ͨ����ǰ�����ͱ���ͨ����36����֤����ҳ��/API/ɸѡ/����/����/�쳣������4������/�е����⣨Controllerȱʧ+·��δע��+Service����ȱʧ+VO����������
- **����**��W5


## P0-011-002-003-001-002 �� ��֤����

- **���ʱ��**��2026-06-08T01:34
- **״̬**��? �����
- **ժҪ**�����ֻ���P07��һ����ҳ��֤ͨ�������mvn compileͨ����ǰ��currencyrate�����TS����37����֤����ҳ��/API/����У��/��������/�쳣����������5�����⣨2���أ�Controllerȱʧ+check-code�˵�ȱʧ��1�У�����ͣ�ù��ܣ�2�ͣ�debounce����+·��ȷ�ϣ�
- **������**��
  - docs/test-reports/finance-CurrencyRate-frontend-test.md
  - docs/test-reports/finance-CurrencyRate-issues.md
- **����**��W4
- **Git SHA**��(���ύ)

- **������**��P0-012-001-002-001-003
- **��������**����֤Service
- **���ʱ��**��2026-06-08T00:20
- **״̬**��?
- **ժҪ**����дRecruitmentServiceTest��Ԫ���ԣ�30������ȫ��ͨ��������CRUD�������̡��쳣������״̬��תУ�顢����ע����֤���߽糡��
- **������**��
  - src/test/java/com/erp/hrm/service/RecruitmentServiceTest.java
  - docs/test-reports/RecruitmentService-test-report.md
- **����**��W3
## P0-011-001-004-001-003 �� ��֤Service

- **���ʱ��**��2026-06-08T00:32
- **״̬**��? �����
- **ժҪ**��ƾ֤��Service��Ԫ������֤ͨ����38������ȫ��ͨ��������CRUD�������̡�Ψһ��У�顢״̬��ת������У�顢����ע����֤���߽糡�������Ա����Ѹ���
- **������**��
  - src/test/java/com/erp/finance/service/VoucherWordServiceTest.java���Ѵ��ڣ���֤ͨ����
  - docs/test-reports/VoucherWordService-test-report.md����֤���£�
- **����**��W3

---

### P0-011-002-005-001-002 ��֤����

| ���� | ֵ |
|------|-----|
| ���ʱ�� | 2026-06-08T00:35 |
| ״̬ | ? |
| ժҪ | ��֤�����˻�P07��һ����ҳ��52����ȫ��ͨ����·��/����/�༭/У��/�쳣��������vue-tsc + vite build + mvn compile ȫ��ͨ����5�������Ѽ�¼ |
| Git commit | 224107b4 |
| ���� | W6 |

### P0-011-001-003-001-003 ��֤Service

| ���� | ֵ |
|------|-----|
| ���ʱ�� | 2026-06-08T00:45 |
| ״̬ | ? |
| ժҪ | AccountService��Ԫ���ԣ�38����������ȫ��ͨ��������CRUD/Ψһ��У��/����У��/����ע��/�߽糡��/ʵ��ת�� |
| Git commit | 0bb65558 |
| ���� | W5 |

### P0-011-002-007-001-002 ��֤����

| ���� | ֵ |
|------|-----|
| ���ʱ�� | 2026-06-08T01:00 |
| ״̬ | ? |
| ժҪ | ��֤��ƿ�ĿP07��һ����ҳ��������鸲��7����֤�㣨·��/���ݼ���/ɸѡ/����/����/У��/�쳣��������6�����⣨2����/2�е�/2�ͣ���vue-tsc����ͨ�� |
| Git commit | 711fb1e8 |
| ���� | W6 |

### P0-011-002-008-001-001 ��д���Ĵ���

| ���� | ֵ |
|------|-----|
| ������ | P0-011-002-008-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-08T10:30 |
| ״̬ | ? |
| ժҪ | ����ƾ֤���б�ҳ(voucherword/index.vue��ͳ�ƿ�Ƭ/������������/VxeTable�������/�����༭��������/״̬�л�/ɾ������ȷ��)��API��(finance-voucherword.ts��CRUD+״̬����+��ҳ��ѯ)��ע��·��/finance/voucherword��������Ӣ��i18n������vite buildͨ�� |
| Git commit | 385831eb |
| ���� | W6 |

#### P0-011-002-009-001-001 ��д���Ĵ���

| ���� | ֵ |
|------|-----|
| ������ | P0-011-002-009-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-08T14:00 |
| ״̬ | ? |
| ժҪ | ƾ֤��P07��һ����ҳ���Ĵ���(��������W6��P0-011-002-008-001-001������ʵ�֣���el-dialog����/�ֶ�У��/�����༭ģʽ/API��/��������/������)��API��(finance-voucherword.ts)������·����ע��(/finance/voucherword)��i18n���������ӣ�vue-tsc���ͼ��ͨ�� |
| Git commit | 30eea751 |
| ���� | W4 |

#### P0-011-002-009-001-002 ��֤����

| ���� | ֵ |
|------|-----|
| ������ | P0-011-002-009-001-002 |
| �������� | ��֤���� |
| ���ʱ�� | 2026-06-08T01:30 |
| ״̬ | ? |
| ժҪ | ƾ֤��P07��һ����ҳ��֤ͨ����·��/API��Լ/���ݼ���/ɸѡ����/�༭����/����У��/������7��ȫ��ͨ��������ǰ����֤����+�����嵥2���ĵ���ҳ����������ʹ��� |
| Git commit | 6b7636eb |
| ���� | W6 |

#### P0-011-001-001-001-001 ��д�ӿڶ���Service�ӿ�

| ���� | ֵ |
|------|-----|
| ������ | P0-011-001-001-001-001 |
| �������� | ��д�ӿڶ���Service�ӿ� |
| ���ʱ�� | 2026-06-08T01:42 |
| ״̬ | ? |
| ժҪ | ��֤ICurrencyRateService�ӿ��Ѵ���������(�̳�IServiceX+CRUD����+@Transactional+@Valid+JavaDoc)��mvn compileͨ�� |
| Git commit | bbcc0ea0 |
| ���� | W6 |

#### P0-011-002-011-001-001 ��дDDL+Entity/Mapper

| ���� | ֵ |
|------|-----|
| ������ | P0-011-002-011-001-001 |
| �������� | ��дDDL+Entity/Mapper |
| ���ʱ�� | 2026-06-08T19:35 |
| ״̬ | ? |
| ժҪ | ����V20260608001__create_sys_announcement.sql(FlywayǨ��/16�ֶ�+COMMENT) + AnnouncementEntity(�̳�BaseEntity/6ҵ���ֶ�) + AnnouncementMapper(�̳�BaseMapperX) |
| Git commit | (pending-commit) |
| ���� | W8 |

#### P0-011-002-010-002-001 ?

| ���� | ֵ |
|------|-----|
| ������ | P0-011-002-010-002-001 |
| �������� | ʵ�ֻ������ҳ�棨Key����+�б�+Value JSON����+���+ͳ�ƿ�Ƭ�� |
| ���ʱ�� | 2026-06-08T19:45 |
| ״̬ | ? |
| ժҪ | erp-ai-web/src/api/modules/cache.ts(CacheStatsVO/CacheKeyVO���Ͷ���+5��API����) + erp-ai-web/src/views/system/cache/index.vue(ͳ�ƿ�Ƭ4��+Key����+el-table�б�+JSON��ʽ��Value����+��ɾ����ȷ��+����ɾ��+30s�Զ�ˢ��) + ·��ע��erp-ai-web/src/router/modules/static.ts |
| Git commit | 037e5639 |
| ���� | W7 |

#### P0-011-002-011-001-002 ��дService+Controller

| ���� | ֵ |
|------|-----|
| ������ | P0-011-002-011-001-002 |
| �������� | ��дService+Controller |
| ���ʱ�� | 2026-06-08T19:53 |
| ״̬ | ? |
| ժҪ | IAnnouncementService(extends IServiceX, CRUD+getUnreadList+markAsRead) + AnnouncementServiceImpl(CRUDʵ��+δ����ѯ+�Ѷ����) + AnnouncementController(6 RESTful�˵�, CRUD��system:announcement:manageȨ��) + DTO(AnnouncementCreateDTO/UpdateDTO/QueryDTO) + AnnouncementVO + �����Ѷ���¼��DDL+Entity+Mapper + mvn compileͨ�� |
| Git commit | b80d48a3 |
| ���� | W5 |

### P0-012 - HRM������Դ����ģ�鿪��

#### P0-012-001-004-001-001 ��д�ӿڶ���Service�ӿ�

| ���� | ֵ |
|------|-----|
| ������ | P0-012-001-004-001-001 |
| �������� | ��д�ӿڶ���Service�ӿ� |
| ���ʱ�� | 2026-06-08T12:00 |
| ״̬ | ? |
| ժҪ | ����ISalaryService.java�ӿڣ��̳�IServiceX<SalaryEntity>������CRUD����(create/update/delete/getById/pageList)��д������ע@Transactional�����ʹ��@Valid DTO��JavaDocע������ |
| Git commit | 92f4fd6f |
| ���� | W5 |

#### P0-012-001-004-001-002 ��дServiceImplʵ����

| ���� | ֵ |
|------|-----|
| ������ | P0-012-001-004-001-002 |
| �������� | ��дServiceImplʵ���� |
| ���ʱ�� | 2026-06-08T12:30 |
| ״̬ | ? |
| ժҪ | ����SalaryServiceImpl(�̳�ServiceImpl/����CRUD/Ա��+�·�Ψһ��У��/netSalary����/@Transactional+BusinessException+@OperLog)+SalaryMapper |
| Git commit | (pending-commit) |
| ���� | W6 |

#### P0-012-001-004-001-003 ��֤Service

| ���� | ֵ |
|------|-----|
| ������ | P0-012-001-004-001-003 |
| �������� | ��֤Service |
| ���ʱ�� | 2026-06-08T01:31 |
| ״̬ | ? |
| ժҪ | ��дSalaryServiceTest(32����������/9�󳡾�����/JUnit5+Mockito/CRUD+Ψһ��+����+�߽�+ʵ��ת��) + ������֤���� |
| Git commit | (pending-commit) |
| ���� | W5 |

#### P0-012-001-005-001-001 ��д���Ĵ���

| ���� | ֵ |
|------|-----|
| ������ | P0-012-001-005-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-08T19:43 |
| ״̬ | ? |
| ժҪ | ����HrmWorkbenchAggregateServiceImpl(@Cacheable�ۺϲ�ѯ/KPI��Ƭ+ͼ����������/���⻧����/�쳣����) + HrmWorkbenchAggregateVO(10���ۺ�ָ��) |
| Git commit | (pending-commit) |
| ���� | W10 |

---

#### P0-006-001-002-001-001 ��д�ӿڶ���Service�ӿ�

| ���� | ֵ |
|------|-----|
| ������ | P0-006-001-002-001-001 |
| �������� | ��д�ӿڶ���Service�ӿ� |
| ���ʱ�� | 2026-06-08T01:33 |
| ״̬ | ? |
| ժҪ | ����OrgDepartmentService�ӿ�(6����) + DeptCreateDTO/DeptUpdateDTO/DeptQueryDTO(3DTO) + DeptListVO/DeptTreeVO/DeptDetailVO(3VO)������ͨ�� |
| Git commit | 5a7ac3c1 |
| ���� | W7 |

#### P0-006-001-002-001-002 ��дServiceImplʵ����

| ���� | ֵ |
|------|-----|
| ������ | P0-006-001-002-001-002 |
| �������� | ��дServiceImplʵ���� |
| ���ʱ�� | 2026-06-08T01:35 |
| ״̬ | ? |
| ժҪ | ����OrgDepartmentServiceImpl(6����:page/tree/getById/create/update/delete) + OrgDepartmentʵ������deptCode�ֶΣ�����˾У��+����Ψһ��+ѭ�����ü��+��������+�Ӳ���/��λ/Ա��ɾ����飬����ͨ�� |
| Git commit | be90082c |
| ���� | W6 |

#### P0-006-001-004-001-001 ��д���Ĵ���

| ���� | ֵ |
|------|-----|
| ������ | P0-006-001-004-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-08T02:15 |
| ״̬ | ? |
| ժҪ | ����WorkbenchVO(4ָ��+3�ֲ��б�+3�ڲ���)+OrgWorkbenchService�ӿ�+OrgWorkbenchServiceImpl(getWorkbenchData��@Cacheable����/4�����/�������ͷֲ�/��˾����ͳ��/��Ա�ֲ���ռ��)+OrgWorkbenchController(GET /api/org/workbench)+OrgDepartment����deptType�ֶ�,����ͨ�� |
| Git commit | 4fa56441 |
| ���� | W6 |
n#### P0-006-001-004-001-002 ��֤����

| ���� | ֵ |
|------|-----|
| ������ | P0-006-001-004-001-002 |
| �������� | ��֤���� |
| ���ʱ�� | 2026-06-08T02:18 |
| ״̬ | ? |
| ժҪ | OrgWorkbenchServiceImplTest��ǿ(21����ȫͨ��):ԭ15����+CacheAnnotation����unless/tenantId��֤+CacheHitTests(2����)+PermissionAnnotationTests(2���� @SaCheckPermission��֤); OrgWorkbenchController����@SaCheckPermission("org:workbench:query"); compileͨ��, ģ��84����ȫͨ�� |
| Git commit | 47e4e81e |
| ���� | W5 |

| P0-007-001-001-001-003 | ��֤Service | 2026-06-08T01:43 | ? | ProductClassServiceTest(45����ȫͨ��)+@ExtendWith(MockitoExtension)+@Nested����(list/getById/save/update/delete/Transactional/EdgeCases/ToVO)+sortOrder�߽�(0/9999)+parentIdУ��+classNameΨһ��+�ӷ���ɾ�����+transactionע����֤+toVOӳ�� | 1d76436d |

| P0-011-001-001-001-003 | ��֤Service | 2026-06-08T01:54 | ? | W7:CurrencyRateServiceTest��ǿ(44����ȫͨ��)+����3����(null exchangeRate/currencyName+����ɾ����֤)+Mockito+JUnit5+10Ƕ����+���Ա��� | TBD |

| P0-007-001-004-001-002 | ��дServiceImplʵ���� | 2026-06-08T02:22 | ? | ProductControlServiceImpl(CRUD+ҵ��У��:productIdΨһ��/���������кŻ���/��λ������У��ռλ)+Entity/Mapper/DTO/QueryDTO/VO/Service�ӿ�,mvn compileͨ�� | f0be73a0 |

| P0-007-001-003-001-001 | ��д�ӿڶ���Service�ӿ� | 2026-06-08T02:34 | ? | ProductUnitService�ӿ�(extends IServiceX<ProductUnit>)+CRUD(list/getById/save/update/delete)+@Valid+@Transactional + ProductUnitDTO/ProductUnitQueryDTO/ProductUnitVO + ProductUnitʵ��(prod_product_unit) | c29ab253 |

| P0-007-001-002-001-003 | ��֤Service | 2026-06-08T02:36 | ? | ProductServiceTest(58����ȫͨ��)+@ExtendWith(MockitoExtension)+@Nested����(list/getById/save/update/delete/Transactional/ToVO/EdgeCases)+�߽�(����50/����200/��ע500�ַ�)+���״̬��תȫ����+transactionע����֤+toVOӳ��+��Ʒ��������SQL | 5ee51ae6 |

| P0-007-001-004-001-001 | ��д�ӿڶ���Service�ӿ� | 2026-06-08T02:42 | ? | ProductControlService�ӿ�(extends IServiceX<ProductControl>)+list/getById/save/update/delete+@Valid+@Transactional + ProductControlDTO(11�ֶ�)/ProductControlQueryDTO(extends PageQuery)/ProductControlVO(������ֶ�), �ļ��Ѵ�������ʵ��, mvn compileͨ�� | TBD |

| P0-007-001-004-001-003 | ��֤Service | 2026-06-08T02:52 | ? | ProductControlServiceTest(28����ȫͨ��)+@ExtendWith(MockitoExtension)+@Nested����(list/GetById/Save/Update/Delete)+����У��(���������к�)+Ψһ��У��(productId�ظ�)+�߽�(nullֵ/��ֵBigDecimal)+ArgumentCaptor�ֶ�ӳ����֤+productcontrol_test_data.sql(5����������)+mvn testͨ�� | TBD |

## P0-006 - ��֯�ܹ�ģ�鿪��

| P0-006-002-003-001-001 | ��д���Ĵ��� | 2026-06-08T02:47 | ? | CompanyForm.vue(7�����ֶ�:��˾����/���/���ô���/����/ע���ʱ�/��ַ/��ϵ�绰)+���ô���18λ��ʽУ��(����ƥ����)+�༭ģʽGET����+����POST/�༭PUT+������ظ��ύ(loading)+$t()���ʻ�+form reset on close+w:720px el-dialog,vue-tsc����ͨ�� | TBD |

## P0-007-001-003-001-003 ��֤Service (ProductUnitService)

| ���� | ֵ |
|------|-----|
| ������ | P0-007-001-003-001-003 |
| �������� | ��֤Service |
| ���ʱ�� | 2026-06-08T02:47 |
| ״̬ | ? |
| ���� | W5 |
| ժҪ | ��дProductUnitService��Ԫ���ԣ�16����������ȫ��ͨ��������CRUD��������+�߽�����+�쳣���� |
| Git commit | 08611d14 |

### P0-006 ��֯�ܹ�ģ�鿪��
| ������ | �������� | ���ʱ�� | ״̬ | ժҪ | SHA |
|---------|---------|---------|:---:|------|-----|
| P0-006-002-000-001-002 | ��֤���� | 2026-06-08T03:15 | ? | ��֤OrgWorkbench: 12/12ͨ��, �޸�Pinia store���ɺ�ȱʧ·��, ����0���� | 1c98391d |

## P0-008-001-003-001-003 ��֤Service (CustomerService)

| ���� | ֵ |
|------|-----|
| ������ | P0-008-001-003-001-003 |
| �������� | ��֤Service |
| ���ʱ�� | 2026-06-08T03:36 |
| ״̬ | ? |
| ���� | W5 |
| ժҪ | ��дCustomerService��Ԫ���ԣ�42����������ȫ��ͨ��������list(10)/getById(2)/save(8)/update(7)/delete(2)/Transactional(5)/EdgeCase(7)/toVO(1)����������Ψһ��У�顢�����ʽУ�顢�ų���������У�顢��ҳɸѡ������������� |
| Git commit | 7a94f2c1 |

### P0-010-002-000-001-001 KPI��Ƭ�����������ִ��-��ǿ���ƺ�ʱ�䷶Χ��Ӧ��

| ���� | ֵ |
|------|-----|
| ������ | P0-010-002-000-001-001 |
| �������� | KPI��Ƭ������� |
| ���ʱ�� | 2026-06-08T04:20 |
| ״̬ | ? |
| ���� | W5 |
| ժҪ | ��ǿKpiCardArea(ע�빤��̨��������Ӧʱ�䷶Χ�仯/���ưٷֱ�display���̼�ͷ/onUnmounted����watch�����ڴ�й©)+ ��չWarehouseWorkbenchKpiVO(������ѡtrend�ֶ�)+ TypeScript����0���� |
| Git commit | TBD |

---
| ���� | ֵ |
|------|-----|
| ������ | P0-010-002-000-003-001 |
| �������� | ����̨ȫ�������� |
| ���ʱ�� | 2026-06-08T04:05 |
| ״̬ | ? |
| ���� | W5 |
| ժҪ | �޸�KPI����Э��(defineExpose+���󴫲�)+�Ƴ�˫��watch+����������֤����(�ܹ�ͼ/������/7������ȫͨ��)+vue-tsc 0���� |
| Git commit | 8cb96b30 |

---
| ���� | ֵ |
|------|-----|
| ������ | P0-010-002-002-001-002 |
| �������� | ��֤���ܣ���ִ��-��Աѡ������ǿ����֤�� |
| ���ʱ�� | 2026-06-08T06:30 |
| ״̬ | ? |
| ���� | W5 |
| ժҪ | ��֤�ֿⶨ�����ҳ(80����֤75ͨ��/�������������/vue-tsc�����/mvn compileͨ��/7����֤��ȫ����/Ψһ�����ΪControllerȱʧ) |
| Git commit | d7bcceb2 |

---
| ���� | ֵ |
|------|-----|
| ������ | P0-010-002-003-001-001 |
| �������� | ��д���Ĵ��루��λ�����б�ҳ�� |
| ���ʱ�� | 2026-06-08T08:10 |
| ״̬ | ? |
| ���� | W5 |
| ժҪ | ��λ����P04���б�ҳ(ͳ�ƿ�Ƭ/����ɸѡ����300ms/VxeTable�������/�����༭ɾ������ͣ�ò���/el-tag״̬��ǩ)+API���Ͷ���+API����ģ��+·��ע��/warehouse/location+vue-tsc����� |
| Git commit | c249b3b3 |

---

## P1-001 ͨ�õ���������濪��

| �ֶ� | ֵ |
|------|-----|
| ������ | P1-001-001-001-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-08T19:41 |
| ״̬ | ? |
| ���� | W5 |
| ժҪ | ʵ��AuditEngineService(submit+approve)��AuditEngineController��DTOs��ʵ��(SysAuditConfigEntity/SysAuditLogEntity/DocumentStatusEntity)��Mapper��AuditApprovedEvent����Ԫ����6��ȫͨ�� |
| Git commit | (��git log) |

#### P1-001-001-001-001-002 ��֤����

| ���� | ֵ |
|------|-----|
| ������ | P1-001-001-001-001-002 |
| �������� | ��֤���� |
| ���ʱ�� | 2026-06-08T20:18 |
| ״̬ | ? |
| ���� | W5 |
| ժҪ | ��ǿAuditEngineServiceTest(����6������:docNotFound/locked/auditLog�ֶ���֤) + �½�AuditEngineControllerTest(6������) + �½�audit-test-data.sql(6����������)��23������ȫͨ�� |
| Git commit | (��git log) |

#### P0-012-001-005-001-002 ��֤����

| ���� | ֵ |
|------|-----|
| ������ | P0-012-001-005-001-002 |
| �������� | ��֤���� |
| ���ʱ�� | 2026-06-08T19:55 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ��֤HrmWorkbenchAggregateService(����ͨ��/���⻧������ȷ/����������ȷ)������8������(ȱController/ȱ�쳣����/ȫ���������ܷ���/�����ֶδ���/ȱ@CacheEvict��)����д���Ա���������嵥 |
| Git commit | (��git log) |

#### P0-012-002-001-001-001 ��д���Ĵ���

| ���� | ֵ |
|------|-----|
| ������ | P0-012-002-001-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-08T20:15 |
| ״̬ | ? |
| ���� | W8 |
| ժҪ | erp-ai-web/src/api/modules/hrm-workbench.ts(HrmWorkbenchVO/TrendItem����+getHrmWorkbenchApi) + erp-ai-web/src/views/hrm/hrmworkbench/index.vue(KPI��Ƭ6��+ECharts����ͼx2+��ͼx2+Refresh+������+��Ӧʽ) + src/.../hrm/controller/HrmWorkbenchController.java(��¶/api/hrm/workbench�ۺϲ�ѯ�˵�) |
| Git commit | (��git log) |

#### P0-012-002-002-001-001 ��д���Ĵ���

| ���� | ֵ |
|------|-----|
| ������ | P0-012-002-002-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-08T20:10 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | erp-ai-web/src/api/modules/hrm-employee.ts(EmployeeVO/QueryDTO/CreateDTO����+CRUD 5��API����) + erp-ai-web/src/views/hrm/employeecenter/index.vue(P03�����б�ҳ:ͳ�ƿ�Ƭ4��+��������300ms+VxeTable�������+�����ǩҳ4��+�༭����+ɾ��ȷ��) + src/.../hrm/controller/EmployeeController.java(��¶/api/hrm/employee CRUD�˵�) + erp-ai-web/src/router/modules/static.ts(·��/hrm/employeecenterע��) + erp-ai-web/src/i18n/locales/zh-CN+en-US/common.ts(HRMԱ�����ʻ�����) |
| Git commit | (��git log) |

#### P0-012-002-003-001-001 ��д���Ĵ���

| ���� | ֵ |
|------|-----|
| ������ | P0-012-002-003-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-08T22:54 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | �����༭����ΪP06�������ӱ���(900px), ��������֤��/����/��λ�ֶ�, ����Ա������Vxe Table�ӱ�(ѧ��/רҵ/��ҵԺУ/������ϵ��/���п���), ֧�����ڵ���༭+��ɾ��, API������EmployeeArchiveDTO����, ��ǿ����У�����(����֤18λ/�ֻ���11λ/�����ʽ) |
| Git commit | 4308aabe |

#### P0-012-002-003-001-002 ��֤����

| ���� | ֵ |
|------|-----|
| ������ | P0-012-002-003-001-002 |
| �������� | ��֤���� |
| ���ʱ�� | 2026-06-08T23:10 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ��֤P06���ӱ���ҳ7���������, ǰ�����ͼ��ͨ��, �޸�P06����i18n keyȱʧ(11��zh-CN/en-US), �޸���������������Դ(����deptTree API), ���ֺ��ȱʧarchives֧��(�Ѽ�¼), ���²��Ա���2�� |
| Git commit | (��git log) |

#### P0-012-002-004-001-001 ��д���Ĵ���

| ���� | ֵ |
|------|-----|
| ������ | P0-012-002-004-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-08T23:45 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ʵ��Ա������P04��һ�б�ҳ: ����hrm-archive APIģ��(6���ӿں���), employeearchive Vueҳ��(VxeTable�������+��������+ͳ�ƿ�Ƭ+CRUD����), ע��·��/hrm/employeearchive, ����i18n��Ӣ��22��key, vite buildͨ�� |
| Git commit | (��git log) |

### P1-001 - ͨ�õ���������濪��

#### P1-001-001-002-001-001 ��д���Ĵ���

| ���� | ֵ |
|------|-----|
| ������ | P1-001-001-002-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-08T19:59 |
| ״̬ | ? |
| ���� | W7 |
| ժҪ | ʵ�ַ�������(unconfirm): AuditEngineService����unconfirm����(postgres�м���+redis�ֲ�ʽ��+���ε��ݼ��+״̬����2��0), ����DownstreamChecker�ӿ�/DownstreamCheckResult/DTO, AuditConfigServiceע�����μ����, Controller����unconfirm�˵�, 5����Ԫ����ȫͨ�� |
| Git commit | (��git log) |

#### P1-001-001-002-001-002 ��֤����

| ���� | ֵ |
|------|-----|
| ������ | P1-001-001-002-001-002 |
| �������� | ��֤���� |
| ���ʱ�� | 2026-06-08T20:30 |
| ״̬ | ? |
| ���� | W8 |
| ժҪ | ��֤����ӿ�11�����: ��������/�����־��֤/��������/�����μ����/4�ַ������״̬�ܾ�(�ݸ�/���ύ/�Ѳ���/������)/�����ڵ���/����ͻ/��������֤, mvn test 29/29ͨ��, ��testData SQL�ű� |

#### P1-001-001-003-001-001 ��д���Ĵ���

| ���� | ֵ |
|------|-----|
| ������ | P1-001-001-003-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-08T22:48 |
| ״̬ | ? |
| ���� | W8 |
| ժҪ | ʵ??��������(voidDocument): AuditEngineService����voidDocument����(Redis�ֲ�ʽ��+PostgreSQL�м���+״̬У��Draft(0)/Submitted(1)/Approved(2)��Voided(4)+�����־+����VoidResourceReleaseEvent), ����AuditVoidDTO/VoidResourceReleaseEvent, Controller����POST /api/engine/audit/void�˵�, 9����Ԫ����ȫͨ��(37/37�ܲ���ͨ��) |
| Git commit | (��git log) |
| Git commit | (��git log) |

#### P1-001-001-003-001-002 ��֤����

| ���� | ֵ |
|------|-----|
| ������ | P1-001-001-003-001-002 |
| �������� | ��֤���� |
| ���ʱ�� | 2026-06-09T00:21 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ��֤void���Ͻӿ�: ����VoidResourceReleaseEventTest(11������:5���¼��ṹ��֤+6��DTO����У�鸲��null/��/�ո�+����@NotBlank/@NotNull), AuditEngineControllerTest����void�˵�2������, ����void-test-data.sql(8����������), ȫ��audit����51������0ʧ�� |
| Git commit | (��git log) |

#### P1-001-001-004-001-001 ��д���Ĵ���

| ���� | ֵ |
|------|-----|
| ������ | P1-001-001-004-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-09T00:54 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ʵ�ֳ�����������(cancelVoid): AuditEngineService����cancelVoid����(Redis�ֲ�ʽ��+PostgreSQL�м���+״̬У��Voided(4)���ָ�������ǰԭʼ״̬+��ѯsys_audit_log��ȡVOID������from_status+�����־+����CancelVoidResourceRestoreEvent), ����CancelVoidResourceRestoreEvent, AuditLogMapper����findPreviousStatusBeforeVoid��ѯ, Controller����POST /api/engine/audit/cancel-void�˵�, 12����Ԫ����ȫͨ��(ȫ��audit����63������0ʧ��) |
| Git commit | (��git log) |

#### P1-001-001-004-001-002 ��֤����

| ���� | ֵ |
|------|-----|
| ������ | P1-001-001-004-001-002 |
| �������� | ��֤���� |
| ���ʱ�� | 2026-06-09T01:01 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ��֤�������Ͻӿ�: AuditEngineCancelVoidTest(11����Ԫ���Ը������������ָ�0/1/2����״̬+��������״̬0/1/2/3����+���ݲ�����+����ͻ+��VOID��־�쳣), ����CancelVoidResourceRestoreEventTest(11�����Ը����¼��ṹ��֤+AuditOperationDTO����У��), ����cancel-void-test-data.sql(7������+����VOID��־��), ȫ��22������0ʧ��BUILD SUCCESS |
| Git commit | (��git log) |

#### P1-001-001-005-001-001 ��д���Ĵ���

| ���� | ֵ |
|------|-----|
| ������ | P1-001-001-005-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-09T01:10 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ʵ�����������P1-002�������̼���: ����AuditApprovalIntegrationService(����ʵ������+JSON���ý���), ApprovalCallbackImpl(����ͨ��/���ػص�+�ݵȴ���), ApprovalFlowConfig/ApprovalNodeģ��, SysAuditConfigEntity����approvalFlowConfig�ֶ�, ApprovalIntegrationTest(11����Ԫ����ȫͨ��) |
| Git commit | (��git log) |

#### P1-001-001-005-001-002 ��֤����

| ���� | ֵ |
|------|-----|
| ������ | P1-001-001-005-001-002 |
| �������� | ��֤���� |
| ���ʱ�� | 2026-06-09T01:18 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ��֤����������������̼���: ����ApprovalIntegrationTest(����3�ڵ����ý���+��������֤+��ģ�鳬ʱ�ع�����+�����־�����֤), ����ApprovalCallbackTest(����ͨ��/���ػص�+�ݵ���֤+�����־��ȷ��֤), ����approval-integration-test-data.sql(5������+5������״̬), ȫ��81������0ʧ��BUILD SUCCESS |
| Git commit | (��git log) |

#### P1-001-001-006-001-001 ��д���Ĵ���

| ���� | ֵ |
|------|-----|
| ������ | P1-001-001-006-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-09T01:28 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ʵ�����������ϵͳ��������: ����SysAuditConfigUpdateDTO+DocCreatedEvent+AuditParamConstants+AuditConfigController+AutoConfirmListener, ����AuditConfigService(updateConfig����У��+Redis����ʧЧ+getConfig���洩͸), ����AuditConfigServiceTest(6����Ԫ���Ը��ǻ���У��/����/�쳣����), ȫ��87������0ʧ��BUILD SUCCESS |
| Git commit | (��git log) |

#### P1-001-001-006-001-002 ��֤����

| ���� | ֵ |
|------|-----|
| ������ | P1-001-001-006-001-002 |
| �������� | ��֤���� |
| ���ʱ�� | 2026-06-09T01:38 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ���������ϵͳ����������֤: ��ǿAuditConfigServiceTest(����8������:null����У��+����д��+���μ��������), �½�AutoConfirmListenerTest(5������:�Զ�ȷ��+δ����+�쳣����), �½�config-test-data.sql(5����������), ȫ��98������������0ʧ��BUILD SUCCESS |
| Git commit | (��git log) |

### P0-011-002-011-001-003 ��֤�������

| ���� | ֵ |
|------|-----|
| ������ | P0-011-002-011-001-003 |
| �������� | ��֤������� |
| ���ʱ�� | 2026-06-08T20:00 |
| ״̬ | ? |
| ���� | W5 |
| ժҪ | ��֤ϵͳ������ȫ��·: DDL(2��)+Entity(2)/Mapper(2)/DTO(3)/VO(1)/Service(�ӿ�+ʵ��)/Controller(6�˵�), mvn compileͨ��, 7����֤�嵥(CRUD/�ö�����/δ���б�/�Ѷ�����ݵ�/����API/��������/Ȩ�޿���)ȫ��ͨ��, �����֤����docs/test-reports/announcement-management-test.md |
| Git commit | (��git log) |

### P0-012-002-001-001-002 ��֤HRM����̨����

| ���� | ֵ |
|------|-----|
| ������ | P0-012-002-001-001-002 |
| �������� | ��֤���� |
| ���ʱ�� | 2026-06-08T20:20 |
| ״̬ | ? |
| ���� | W8 |
| ժҪ | ��֤HRM����̨P02ǰ��: ���ҳ�����(KPI��Ƭ+4��EChartsͼ��+��̬����), ���ֲ��޸�3������(·��ȱʧ/i18n����ȱʧ/Ӳ��������), vue-tsc+vite buildͨ��, �����֤����������嵥 |
| Git commit | (��git log) |

### P0-012-002-002-001-002 ��֤Ա������P03�����б�ҳ����

| ���� | ֵ |
|------|-----|
| ������ | P0-012-002-002-001-002 |
| �������� | ��֤���� |
| ���ʱ�� | 2026-06-08T20:25 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ��֤Ա������P03�����б�ҳ: ������鸲��7����֤�嵥(·��/���ݼ���/ɸѡ/CRUD/����/У��/�쳣����), ǰ��pnpm build����������, ����5������(deptOptions������Դ/stats�ͻ��˼��㲻׼/3��i18n��©/ȱ�ٲ��Ÿ�λ�ֶ�), �����֤����+�����嵥 |
| Git commit | (��git log) |

| ���� | ֵ |
|------|-----|
| ������ | P0-012-002-003-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-08T22:55 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ʵ��HRMԱ������P06���ӱ���ҳ: ����dialogΪ����P06����(900px), ��������֤��/����/��λ�ֶμ���ʽУ��, ����Ա������Vxe Table�ӱ�(ѧ��/רҵ/��ҵԺУ/������ϵ��/���п���)֧�����ڵ���༭+��ɾ��, �������������ύ, API������EmployeeArchiveDTO����, ���mvn compileͨ��, ǰ�����������ʹ��� |
| Git commit | (��git log) |

| ���� | ֵ |
|------|-----|
| ������ | P0-011-002-011-002-001 |
| �������� | ʵ�ֹ������ҳ |
| ���ʱ�� | 2026-06-09T00:00 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ʵ��ϵͳ�������P04��һ�б�ҳ: announcement/index.vueҳ��(����/����/ʱ��/�ö�/״̬����+����ɸѡ+�����༭Dialog+�ö��л�+ɾ������ȷ��), announcement.ts APIģ��(getAnnouncementPageList/createAnnouncement/updateAnnouncement/deleteAnnouncement), announcement.ts���Ͷ���, ·��ע��/system/announcement, ǰ��pnpm buildͨ�� |
| Git commit | (��git log) |

#### P0-011-002-011-002-002 ʵ�ֹ���֪ͨ����

| ������ | P0-011-002-011-002-002 |
| �������� | ʵ�ֹ���֪ͨ��������¼�󵯴�+δ������+�Ѷ����+������ת�� |
| ���ʱ�� | 2026-06-08T21:00 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ʵ�ֹ���֪ͨ����: AnnouncementPopup.vue(��¼���Զ�����δ������/�������Ѷ�����ת/sessionStorage���ظ�����), announcement.ts����getUnreadList/markAsRead API, announcement.ts Piniaȫ��store����δ����badge, AppLayout.vue���ع��浯��, Navbar.vue����badgeʵʱͬ��, ǰ��pnpm buildͨ�� |
| Git commit | (��git log) |


### P0-012-002-004-001-002 ��֤Ա������P04��һ�б�ҳ����

| ������ | P0-012-002-004-001-002 |
| �������� | ��֤���� |
| ���ʱ�� | 2026-06-08T23:31 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ��֤Ա������P04��һ�б�ҳ: ������鸲��7����֤�嵥(·��/���ݼ���/ɸѡ/CRUD/����/У��/�쳣����), ����2����������(���APIȱʧ+���ݿ��ȱʧ)��4���е�������, �����֤����hrm-EmployeeArchive-frontend-test.md+�����嵥hrm-EmployeeArchive-issues.md |
| Git commit | f55357f2 |

### P0-012-002-005-001-001 ��д��Ƹ����P04��һ�б�ҳ���Ĵ���

| ������ | P0-012-002-005-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-08T23:45 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ������Ƹ����P04��һ�б�ҳ: API��(hrm-recruitment.ts��6���ӿ�+�������Ͷ���)+ҳ�����(recruitment/index.vue��ͳ�ƿ�Ƭ/����ɸѡ/CRUD����/VxeTable/����)+·��ע��(/hrm/recruitment)+��Ӣ��i18n����(22��), ��ѭԱ������P04ҳ��ģʽ |
| Git commit | 2e5f56c5 |

### P0-012-002-005-001-002 ��֤��Ƹ����P04��һ�б�ҳ����

| ������ | P0-012-002-005-001-002 |
| �������� | ��֤���� |
| ���ʱ�� | 2026-06-08T21:00 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ��֤��Ƹ����P04��һ�б�ҳ: ������鸲��7����֤�嵥(·��/���ݼ���/ɸѡ/CRUD/����/У��/�쳣����), ǰ��TS����ͨ��+���Maven����ͨ��, ����3����������(RecruitmentControllerȱʧ+ǰ��˲�ѯDTO�ֶβ�ƥ��+ServiceȱupdateStatus����)��2���е�������(״̬�л���2̬+statusѡ��δ���ʻ�), �����֤����hrm-Recruitment-frontend-test.md+�����嵥hrm-Recruitment-issues.md |
| Git commit | �� |

### P0-012-002-006-001-001 ��д���ڹ���P04��һ�б�ҳ���Ĵ���

| ������ | P0-012-002-006-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-08T21:15 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | �������ڹ���P04��һ�б�ҳ: API��(hrm-attendance.ts��5��CRUD�ӿ�+�������Ͷ���)+ҳ�����(attendance/index.vue��ͳ�ƿ�Ƭ/����ɸѡ/CRUD����/VxeTable�������/300ms����)+·��ע��(/hrm/attendance), ��ѭ��Ƹ����P04ҳ��ģʽ, Vite buildͨ�� |

### P0-012-002-006-001-002 ��֤���ڹ���P04��һ�б�ҳ����

| ������ | P0-012-002-006-001-002 |
| �������� | ��֤���� |
| ���ʱ�� | 2026-06-08T21:45 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ��֤���ڹ���P04��һ�б�ҳ: �������+��̬����+������֤����7����֤�嵥(·��/���ݼ���/ɸѡ/CRUD/����/У��/�쳣����), ���Maven����ͨ��, ����2����������(AttendanceControllerȱʧ+27��i18n�����ȱʧ)��2����������(ͳ�ƿ�Ƭ����ǰҳ+��ϢӲ����), �����֤����hrm-Attendance-frontend-test.md+�����嵥hrm-Attendance-issues.md |

### P0-012-002-007-001-001 ��дн�ʹ���P03�����б�ҳ���Ĵ���

| ������ | P0-012-002-007-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-09T00:10 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����н�ʹ���P03�����б�ҳ: API��(hrm-salary.ts��5��CRUD�ӿ�+�������Ͷ���ƥ��SalaryVO/SalaryQueryDTO/SalaryCreateDTO��˽ṹ)+ҳ�����(salary/index.vue��ͳ�ƿ�Ƭ/����ɸѡ/���Ӳ���-VxeTable����+el-tabs�ӱ���ϸ�ֽ�/CRUD����/300ms����/ʵ������Ԥ��/����ʽ��), vue-tsc���ͼ��ͨ�� |

### P0-012-002-007-001-002 ��֤н�ʹ���P03�����б�ҳ����

| ������ | P0-012-002-007-001-002 |
| �������� | ��֤���� |
| ���ʱ�� | 2026-06-09T00:45 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ��̬���������֤н�ʹ���P03�����б�ҳ: 7����֤�嵥��3��ͨ��/2������/2�����֤; ����6������(����: ȱ��SalaryController+·��δע��+i18nȱʧ; ����: �˵�ȱʧ+Ȩ��ָ��ȱʧ+����������һ��); �������Ա���+�����嵥 |

### P0-012-002-008-001-001 ��дн�ʹ���P06���ӱ���ҳ���Ĵ���

| ���� | ֵ |
|------|-----|
| ������ | P0-012-002-008-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-09T01:15 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����SalaryController(5��CRUD�˵�+Swaggerע��)+��дsalary/index.vueΪP06���ӱ���ҳ(�󵯴�960px+������8�ֶκ�Ա��/���/�¶�/��������/�Ӱ��/����/�ۿ�/����״̬+��ϸ�ӱ�VxeTable�����༭����Ŀ����/����/���+���������ύ+��������+ͳ�ƿ�Ƭ+P03�������)+����hrm-salary.ts(����SalaryDetailItem+��չDTO�ֶ�)+����i18n��Ӣ�ķ���(35��)+ע��·��HRM_SALARY,����ͨ�� |

### P0-012-002-008-001-002 ��֤н�ʹ���P06���ӱ���ҳ����

| ���� | ֵ |
|------|-----|
| ������ | P0-012-002-008-001-002 |
| �������� | ��֤���� |
| ���ʱ�� | 2026-06-09T01:30 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ��֤7���������ȫ��ͨ��(·��/���ݼ���/ɸѡ����/��������/���ݻ���/����У��/�쳣����)+�޸�netSalaryPreview���ʹ���+���ǰ����֤����(hrm-Salary-frontend-test.md)+�����嵥(hrm-Salary-issues.md)+��˱���ͨ��+ǰ�˱���ͨ��(��2��vue-tscģ��ref��) |

### P0-012-002-009-001-001 ��дԱ������P07��һ����ҳ���Ĵ���

| ���� | ֵ |
|------|-----|
| ������ | P0-012-002-009-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-09T01:46 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ��ǿԱ������P07����ҳ:Ա��Զ������ѡ����/����5�������ֶ�(��ϵ�绰+סַ+���п�+������+�籣)/�༭ģʽGET API�������/�ֻ��Ÿ�ʽУ��/��Ӣ�Ĺ��ʻ�����/ǰ�˱���ͨ�� |

### P0-012-002-009-001-002 ��֤Ա������P07��һ����ҳ����

| ���� | ֵ |
|------|-----|
| ������ | P0-012-002-009-001-002 |
| �������� | ��֤���� |
| ���ʱ�� | 2026-06-09T02:20 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | �������+������֤7���������:·��?/���ݼ���?(���APIȱʧ)/ɸѡ?/����?/����?/У��??(����)/�쳣?;�����֤����(hrm-EmployeeArchive-frontend-test.md)+�����嵥(hrm-EmployeeArchive-issues.md);����:���EmployeeArchive API+���ݿ��δʵ�� |
| Git SHA | 03007df1 |

### P0-012-002-010-001-001 ��д��Ƹ��������ҳ���Ĵ���

| ���� | ֵ |
|------|-----|
| ������ | P0-012-002-010-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-09T02:00 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ��ǿ��Ƹ����P07����ҳ:��������ѡ����(getDeptTree)/������λҪ���ֶ�(requirements el-textarea+�б���)/�༭ģʽ����GET API�������/��ֹ���ڽ������ڽ���/���Ʊ���У�����(���ű���+����������+��ֹ����У��)/API DTO������չrequirements�ֶ�/��Ӣ�Ĺ��ʻ�����/ǰ�˱���ͨ�� |

### P0-012-002-010-001-002 ��֤��Ƹ����P07��һ����ҳ����

| ���� | ֵ |
|------|-----|
| ������ | P0-012-002-010-001-002 |
| �������� | ��֤���� |
| ���ʱ�� | 2026-06-09T02:10 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | �������+������֤7���������:·��?/���ݼ���?(���Controllerȱʧ)/ɸѡ?/����?/����?/У��?/�쳣?;����ҳר����֤:��������ѡ����?/��λҪ���ֶ�?/����˫��У��?/�༭GET API����+fallback?;�����֤����+�����嵥(8������:3��P0�������+3��P1�е�����+2��P2����) |
| Git SHA | d0e79c01 |



### P0-012-002-011-001-001 ��д���ڹ�������ҳ���Ĵ���

| ���� | ֵ |
|------|-----|
| ������ | P0-012-002-011-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-09T02:30 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ��ǿ���ڹ���P07��һ����ҳ:��Աѡ����(el-selectԶ���������input-number)+�°�>�ϰ�ʱ��У��(checkoutAfterCheckIn validator)+Ա��API����(getEmployeePageApi)+������Ӣ��i18n(35��attendance����)+��������/�ύloading/��������/VxeTable�������;ǰ�˱���ͨ�� |


### P0-012-002-011-001-002 ��֤���ڹ���P07��һ����ҳ����

| ���� | ֵ |
|------|-----|
| ������ | P0-012-002-011-001-002 |
| �������� | ��֤���� |
| ���ʱ�� | 2026-06-09T02:35 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ��̬�������+�ṹ������֤����P07����ҳ:7���������(·��?/���ݼ���?/ɸѡ?/����?/����?/У��?/�쳣?);����6������(2 CRITICAL:useI18nδ�����������+���AttendanceControllerȱʧ;1 HIGH:stats��ͳ�Ƶ�ǰҳ;1 MEDIUM:spread˳�����;2 LOW:checkInTime��У��+v-permissionȱʧ);�����֤����+�����嵥���޸����� |
| Git SHA | a224c48d |

### P1-002 - ��������ģ�鿪��

#### P1-002-001-001-001-001 ��д�ӿڶ���Service�ӿ�

| ���� | ֵ |
|------|-----|
| ������ | P1-002-001-001-001-001 |
| �������� | ��д�ӿڶ���Service�ӿ� |
| ���ʱ�� | 2026-06-09T02:40 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����IApprovalDefinitionService�ӿ�(5����: pageList/getById/create/update/delete)+ApprovalDefinitionEntityʵ��+4��DTO(NodeCreateDTO/DefinitionCreateDTO/DefinitionQueryDTO/DefinitionUpdateDTO)+3��VO(DefinitionListVO/DefinitionDetailVO/NodeVO);mvn compileͨ�� |
| Git SHA | (��git log) |

#### P1-002-001-001-001-002 ��дServiceImplʵ����

| ���� | ֵ |
|------|-----|
| ������ | P1-002-001-001-001-002 |
| �������� | ��дServiceImplʵ���� |
| ���ʱ�� | 2026-06-09T02:50 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����ApprovalDefinitionServiceImpl(CRUD����ʵ��:pageList/getById/create/update/delete)+ApprovalDefinitionMapper;@Transactional�������;BusinessException�쳣����;�����ظ�У��;mvn compileͨ�� |
| Git SHA | (��git log) |

#### P1-002-001-001-001-003 ��֤Service

| ���� | ֵ |
|------|-----|
| ������ | P1-002-001-001-001-003 |
| �������� | ��֤Service |
| ���ʱ�� | 2026-06-09T02:51 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����ApprovalDefinitionServiceTest(14����Ԫ���Ը���CRUD+�쳣����+������ͻ+����ع�);mvn testȫ��ͨ�� |
| Git SHA | (��git log) |

#### P1-002-001-002-001-001 ��д���Ĵ���

| ���� | ֵ |
|------|-----|
| ������ | P1-002-001-002-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-09T03:00 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����ApprovalInstanceEntity/ApprovalRecordEntity/InstanceCreateDTO/RecordActionDTO/InstanceQueryDTO/InstanceVO/RecordVO/ApprovalInstanceMapper/ApprovalRecordMapper/IApprovalInstanceService/ApprovalInstanceServiceImpl/IApprovalRecordService/ApprovalRecordServiceImpl/ApprovalInstanceController/ApprovalRecordController��15���ļ�;ʵ������ʵ���ύ/����/��ҳ��ѯ+������¼����(ͨ��/����);@Transactional����һ����;BusinessException�쳣��ʾ;mvn compileͨ�� |
| Git SHA | (��git log) |

#### P1-002-001-002-001-002 ��֤����

| ���� | ֵ |
|------|-----|
| ������ | P1-002-001-002-001-002 |
| �������� | ��֤���� |
| ���ʱ�� | 2026-06-09T03:06 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����ApprovalInstanceServiceTest(18������)+ApprovalRecordServiceTest(12������)�����ύ/����/��ҳ��ѯ/��������+�߽�����+������ͻ+�쳣����;30/30 tests passed;mvn testͨ��;�������Ա��� |
| Git SHA | (��git log) |

#### P1-002-001-003-001-001 ��д���Ĵ���

| ���� | ֵ |
|------|-----|
| ������ | P1-002-001-003-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-09T03:15 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����ApprovalRecordCoreService(ת��/��ǩ/�߰�)+3����DTO(RecordTransferDTO/RecordCountersignDTO/RecordUrgeDTO)+����ApprovalRecordController(3���¶˵�);��������״̬��ʵ��;mvn compileͨ�� |
| Git SHA | (��git log) |

#### P1-002-001-003-001-002 ��֤����

| ���� | ֵ |
|------|-----|
| ������ | P1-002-001-003-001-002 |
| �������� | ��֤���� |
| ���ʱ�� | 2026-06-09T03:17 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����ApprovalRecordCoreServiceTest(16������:ת��/��ǩ/�߰�)+��ǿApprovalRecordServiceTest(����comment�߽����)+���Ա���;29������ȫ��ͨ��;BUILD SUCCESS |
| Git SHA | (��git log) |

#### P1-002-001-004-001-001 ��д���Ĵ���

| ���� | ֵ |
|------|-----|
| ������ | P1-002-001-004-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-09T03:25 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����MyApprovalQueryDTO/MyApprovalVO/IApprovalMyService/ApprovalMyServiceImpl/ApprovalMyController;ʵ���ҵ�������ѯ�ӿ�(����/����/�ҵ�����)��Tab��ҳ��ѯ;mvn compileͨ�� |
| Git SHA | (��git log) |

#### P1-002-001-004-001-002 ��֤����

| ���� | ֵ |
|------|-----|
| ������ | P1-002-001-004-001-002 |
| �������� | ��֤���� |
| ���ʱ�� | 2026-06-09T03:28 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����ApprovalMyServiceTest(12����Ԫ����: ����/����/�ҵ�������Tab��ѯ+�߽�����+��������); ��д���Ա���; mvn testȫ��ͨ�� |
| Git SHA | (��git log) |

#### P1-002-001-005-001-001 ��д���Ĵ���

| ���� | ֵ |
|------|-----|
| ������ | P1-002-001-005-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-09T03:36 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ʵ������ͳ�ƽӿ�: ApprovalStatisticsVO(ͳ�����ݽṹ), IApprovalStatisticsService(�ӿ�), ApprovalStatisticsServiceImpl(��״̬/����/����ά��ͳ��), ApprovalStatisticsController(/api/approval/statistics); mvn compileͨ�� |
| Git SHA | 59c9da03 |

#### P1-002-001-005-001-002 ��֤����

| ���� | ֵ |
|------|-----|
| ������ | P1-002-001-005-001-002 |
| �������� | ��֤���� |
| ���ʱ�� | 2026-06-09T04:00 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ��дApprovalStatisticsServiceTest(8����Ԫ����:��������2+�߽�5+�쳣1,ȫ��PASS); �������Ա���; mvn compile+testͨ�� |
| Git SHA | 8ab61544 |

#### P1-002-001-006-001-001 ��д���Ĵ���

| ���� | ֵ |
|------|-----|
| ������ | P1-002-001-006-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-09T04:35 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ʵ��������������ʱ����: ApprovalWorkflowRuntimeService(״̬������:����/�ƽ�/���/����/����+flowConfig JSON����+@Transactional����), ApprovalWorkflowController(/api/approval/runtime/*����ʱAPI); mvn compileͨ�� |
| Git SHA | 01f0a021 |

#### P1-002-001-006-001-002 ��֤����

| ���� | ֵ |
|------|-----|
| ������ | P1-002-001-006-001-002 |
| �������� | ��֤���� |
| ���ʱ�� | 2026-06-09T05:00 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ��дApprovalWorkflowRuntimeServiceTest(21����Ԫ����:����3+�ƽ�5+���1+����1+����4+״̬��ѯ2+�߽�4+����1,ȫ��PASS); �������Ա���; mvn compile+test 102����ȫͨ�� |
| Git SHA | 24ebfe86 |

#### P1-002-001-007-001-001 ��д�����ļ���

| ���� | ֵ |
|------|-----|
| ������ | P1-002-001-007-001-001 |
| �������� | ��д�����ļ��� |
| ���ʱ�� | 2026-06-09T05:45 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����ApprovalUrgeProperties������(�߰췽ʽ/Ƶ��/�Զ���ʱ/������������), ׷��application.yml�����߰����ö�; mvn compileͨ�� |
| Git SHA | c1c5b11c |

#### P1-002-001-007-001-002 ��֤��д����������

| ���� | ֵ |
|------|-----|
| ������ | P1-002-001-007-001-002 |
| �������� | ��֤��д���������� |
| ���ʱ�� | 2026-06-09T06:02 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ��дApprovalUrgePropertiesTest(15������: Beanע��/Ĭ��ֵ��/�ۺϰ�/У��Լ��/ǰ׺��֤), ȫ��117������ģ�����ͨ��, 0ʧ��0���� |
| Git SHA | 42fcbb3a |

#### P1-002-001-008-001-001 ��д�����ļ�/��
| ���� | ֵ |
|------|-----|
| ������ | P1-002-001-008-001-001 |
| �������� | ��д�����ļ�/�� |
| ���ʱ�� | 2026-06-09T06:10 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����ApprovalDelegateProperties.java(@ConfigurationProperties��approval.delegate.*����:enabled/maxDelegateDays/autoRevoke/notification.enabled/allowRedelegate)+application.yml����approval.delegate���ö�,mvn compileͨ�� |
| Git SHA | 91ddca69 |

#### P1-002-001-008-001-002 ��֤��д����������
| ���� | ֵ |
|------|-----|
| ������ | P1-002-001-008-001-002 |
| �������� | ��֤��д���������� |
| ���ʱ�� | 2026-06-09T06:21 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����ApprovalDelegatePropertiesTest(12����Ԫ���Ը���:Beanע��/Ƕ�׶���/Ĭ��ֵ/�ۺϰ�/@MinУ��/@ConfigurationPropertiesǰ׺��֤);mvn testȫ��ͨ��,mvn compile BUILD SUCCESS |
| Git SHA | (��git log) |

#### P1-002-001-009-001-001 ��дԤ������SQL INSERT
| ���� | ֵ |
|------|-----|
| ������ | P1-002-001-009-001-001 |
| �������� | ��дԤ������SQL INSERT |
| ���ʱ�� | 2026-06-09T06:35 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����ApprovalDefinitionMapper.xml(�����ֶ��б�/���ӳ��/�������ѯ/��ѯ��������/����INSERTԤ�ö���)����չApprovalDefinitionMapper�ӿ�(3���Զ��巽��);mvn compile BUILD SUCCESS |
| Git SHA | (��git log) |

#### P1-002-001-009-001-002 ʵ��Ԥ�����̼����߼�
| ���� | ֵ |
|------|-----|
| ������ | P1-002-001-009-001-002 |
| �������� | ʵ��Ԥ�����̼����߼� |
| ���ʱ�� | 2026-06-09T04:24 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����ApprovalDefinitionController(CRUD 5���˵�);����ǰ��APIģ��approval.ts(5��API����)�����Ͷ���approval.ts;���������������Vueҳ��(����/�б�/����/�༭/ɾ��);ע��·��;mvn compile BUILD SUCCESS |
| Git SHA | (��git log) |

#### P1-002-001-009-001-003 ��֤Ԥ������
| ���� | ֵ |
|------|-----|
| ������ | P1-002-001-009-001-003 |
| �������� | ��֤Ԥ������ |
| ���ʱ�� | 2026-06-09T04:32 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����ApprovalPresetVerificationTest(10������: batchInsertPreset����/ȥ��/�ݵ�/�������� + CRUD���� + enableFlag����);ȫ��Approvalģ��112������0ʧ��;mvn compile BUILD SUCCESS |
| Git SHA | 30eb590f |
| ���� | ֵ |
|------|-----|
| ������ | P1-002-001-010-001-001 |
| �������� | ��д��־��ѯSQL |
| ���ʱ�� | 2026-06-09T04:38 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����ApprovalRecordMapper.xml(�����ֶ��б�/��־��ѯ�ֶ��б�����JOIN/��̬����Ƭ��/selectLogList��ҳ��ѯ/selectLogCount����/selectLogByInstanceId��·��ѯ);��չApprovalRecordMapper�ӿ�(3����־��ѯ����);mvn compile BUILD SUCCESS |
| Git SHA | (��git log) |

#### P1-002-001-010-001-002 ��дͳ��SQL

| ���� | ֵ |
|------|-----|
| ������ | P1-002-001-010-001-002 |
| �������� | ��дͳ��SQL |
| ���ʱ�� | 2026-06-09T05:00 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����ApprovalInstanceMapper.xml(selectStatistics����ͳ��/selectStatusDistribution״̬�ֲ�/selectDefinitionCounts����ά��ͳ��);��չApprovalInstanceMapper�ӿ�(3��ͳ�Ʒ���);ApprovalRecordMapper����countByApproverId;ApprovalStatisticsServiceImpl��ΪSQL�ۺ�(����ȫ��������ڴ����);mvn compile BUILD SUCCESS |
| Git SHA | (��git log) |

#### P1-002-002-001-001-001 ��д�����ļ���

| ���� | ֵ |
|------|-----|
| ������ | P1-002-002-001-001-001 |
| �������� | ��д�����ļ��� |
| ���ʱ�� | 2026-06-09T02:50 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����ApprovalDefinitionProperties.java(������������������:��ҳ/����/�ڵ�/����4������,bind approval.definition.*);application.yml����approval.definition���ö�(4��16��������);mvn compile BUILD SUCCESS |
| Git SHA | (��git log) |


#### P1-002-001-010-001-003 ��֤��ѯ�ӿ�

| ���� | ֵ |
|------|-----|
| ������ | P1-002-001-010-001-003 |
| �������� | ��֤��ѯ�ӿ� |
| ���ʱ�� | 2026-06-09T04:53 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ��֤������־��ѯ��ͳ�ƽӿ�: mvn compileͨ��, 139������ȫͨ��(�޸�ApprovalStatisticsServiceTest���캯��������ƥ��); ��֤/api/approval/{definition,instance,record,my,statistics}��5��API�˵�; ��־��ѯSQL(selectLogList/LogCount/LogByInstanceId)����JOIN��is_deleted����; ͳ��SQL(selectStatistics/selectStatusDistribution/selectDefinitionCounts)ʹ��SQL�ۺ� |
| Git SHA | (��git log) |


#### P1-002-002-001-001-002 ��֤��д����������

| ���� | ֵ |
|------|-----|
| ������ | P1-002-002-001-001-002 |
| �������� | ��֤��д���������� |
| ���ʱ�� | 2026-06-09T05:10 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ��֤ApprovalDefinitionProperties����������: @ConfigurationProperties��approval.definition.*��ȷ, 4���ڲ�������(Pagination/Sort/Node/Cache)��@ValidatedУ��ע��, application.yml���ö��ֶ���Properties��ȫƥ��(8��Ҷ�Ӽ�ֵ), mvn compileͨ����ERROR |
| Git SHA | (��git log) |


#### P1-002-002-002-001-001 ��д���Ĵ���

| ���� | ֵ |
|------|-----|
| ������ | P1-002-002-002-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-09T05:01 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ʵ������ʵ���б�ҳ: ����InstanceQueryDTO/InstanceCreateDTO/InstanceVO���Ͷ���, ����getInstancePage/getInstanceDetail/submitInstance/withdrawInstance API��װ, ��������ʵ���б�Vueҳ��(����/����/״̬��ǩ/��ҳ/����Ի���/���ز���), ע��/approval/instance·��, vue-tsc�����ʹ��� + mvn compileͨ�� |
| Git SHA | 5b614542 |

#### P1-002-002-002-001-002 ��֤����

| ���� | ֵ |
|------|-----|
| ������ | P1-002-002-002-001-002 |
| �������� | ��֤���� |
| ���ʱ�� | 2026-06-09T05:08 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ��֤����ʵ���б�ҳ����: ����18����Ԫ����ȫ��ͨ��(SubmitTests��5/PageListTests��3/GetByIdTests��2/WithdrawTests��5/BoundaryTests��3), mvn compileͨ��, ��֤�嵥8����6��?2��??(�޸�/ɾ��������������ʵ��), ���ɲ��Ա��� |
| Git SHA | f1a8fdb6 |

#### P1-002-002-003-001-001 ��д���Ĵ���

| ���� | ֵ |
|------|-----|
| ������ | P1-002-002-003-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-08T21:16 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ʵ��������¼�б�ҳ��˺���: ����RecordLogQueryDTO(8ɸѡ�ֶ�)/RecordLogVO(16�ֶ�), IApprovalRecordService����pageLogList����, ApprovalRecordServiceImpl����Mapper selectLogList/selectLogCountʵ�ַ�ҳ��ѯ, ApprovalRecordController����GET /api/approval/record�˵� |
| Git SHA | 5df1a07d |

#### P1-002-002-003-001-002 ��֤����

| ���� | ֵ |
|------|-----|
| ������ | P1-002-002-003-001-002 |
| �������� | ��֤���� |
| ���ʱ�� | 2026-06-09T05:21 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ��֤������¼�б�ҳ����: ����31����Ԫ����ȫ��ͨ��(������2�������ֹ�������), mvn compileͨ��, ��֤�嵥8��ȫ��?, ���ɲ��Ա��� |
| Git SHA | 8c95b0a5 |

#### P1-002-002-004-001-001 ��д���Ĵ���

| ���� | ֵ |
|------|-----|
| ������ | P1-002-002-004-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-09T05:30 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ʵ���ҵ������б�ҳ: ǰ������MyApprovalQueryDTO/MyApprovalVO���Ͷ���, getMyApprovalPage��recordAction API����, �ҵ�����Vueҳ��(��Tab����/����/�ҵ�����+����+��ҳ+���������Ի���+����Ի���), APPROVAL_MY·��ע��; ���MyApprovalController/IApprovalMyService/ApprovalMyServiceImpl�Ѿ��� |
| Git SHA | 66e9804c |

#### P1-002-002-004-001-002 ��֤����

| ���� | ֵ |
|------|-----|
| ������ | P1-002-002-004-001-002 |
| �������� | ��֤���� |
| ���ʱ�� | 2026-06-09T05:35 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ��֤�ҵ������б�ҳ: ����ApprovalMyServiceTestȫ��12����Ԫ����ͨ��, ����ģ��141������ȫͨ��, ��֤�嵥8��ȫ��?, mvn compileͨ��, ǰ����������ر������, ���ɲ��Ա��� |
| Git SHA | 3fc79bd8 |

#### P1-002-002-005-001-001 ��д���Ĵ���

| ���� | ֵ |
|------|-----|
| ������ | P1-002-002-005-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-09T05:50 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ʵ������ͳ��ҳ: ����ApprovalStatisticsVOǰ�����Ͷ���+getStatistics API����+����ͳ��Vueҳ��(ȫ�ָ�����Ƭ+����ͳ��+״̬�ֲ���ͼ+����ά����״ͼecharts)+APPROVAL_STATISTICS·��ע��; mvn compileͨ��; ǰ����������ر������ |
| Git SHA | (��git log) |

#### P1-002-002-005-001-002 ��֤����

| ���� | ֵ |
|------|-----|
| ������ | P1-002-002-005-001-002 |
| �������� | ��֤���� |
| ���ʱ�� | 2026-06-09T06:50 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ��֤����ͳ��ҳ: ���8/8����ͨ��(��������+�߽�+�쳣), ǰ��vue-tsc���ͼ��ͨ��, ǰ���API·����VO����һ���Խ�����֤ͨ��, ���Ա��������� |
| Git SHA | (��git log) |

#### P1-002-002-006-001-001 ��д�����ļ���

| ���� | ֵ |
|------|-----|
| ������ | P1-002-002-006-001-001 |
| �������� | ��д�����ļ��� |
| ���ʱ�� | 2026-06-09T06:55 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | Ϊ�����߰�����ҳ����ǰ�˷�ҳ/��������: ApprovalUrgeProperties����Pagination(defaultPageSize/maxPageSize)��Sort(defaultField/defaultOrder)�ڲ���, application.yml����approval.urge.pagination��approval.urge.sort���ö�; mvn compileͨ��, 15/15����ͨ�� |
| Git SHA | (��git log) |

#### P1-002-002-006-001-002 ��֤��д����������

| ���� | ֵ |
|------|-----|
| ������ | P1-002-002-006-001-002 |
| �������� | ��֤��д���������� |
| ���ʱ�� | 2026-06-09T07:00 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ��֤ApprovalUrgeProperties����������: mvn compileͨ��, 15/15����ͨ��(��ע����֤/Ĭ��ֵ��/У�����/@ConfigurationPropertiesǰ׺��֤), application.yml���ö���Java������ȫ��Ӧ |
| Git SHA | (��git log) |

#### P1-002-002-007-001-001 ��д�����ļ���

| ���� | ֵ |
|------|-----|
| ������ | P1-002-002-007-001-001 |
| �������� | ��д�����ļ��� |
| ���ʱ�� | 2026-06-09T07:10 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | Ϊ����ί������ҳ����ǰ�˷�ҳ/��������: ApprovalDelegateProperties����Pagination(defaultPageSize/maxPageSize)��Sort(defaultField/defaultOrder)�ڲ���, application.yml����approval.delegate.pagination��approval.delegate.sort���ö�; mvn compileͨ�� |
| Git SHA | (��git log) |

#### P1-002-002-007-001-002 ��֤��д����������

| ���� | ֵ |
|------|-----|
| ������ | P1-002-002-007-001-002 |
| �������� | ��֤��д���������� |
| ���ʱ�� | 2026-06-09T08:10 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ��֤ApprovalDelegateProperties����������: mvn compileͨ��, 12/12����ͨ��(��Beanע����֤/Ĭ��ֵ��/Ƕ�׶���ǿ�/@ConfigurationPropertiesǰ׺��֤/@ValidatedУ��), application.yml���ö���Java������ȫ��Ӧ, ����һ������ApprovalUrgeProperties/ApprovalDefinitionProperties���� |
| Git SHA | (��git log) |

#### P1-002-002-008-001-001 ʵ��Ԥ����������ҳ��

| ���� | ֵ |
|------|-----|
| ������ | P1-002-002-008-001-001 |
| �������� | ʵ��Ԥ����������ҳ�� |
| ���ʱ�� | 2026-06-09T08:30 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ʵ��Ԥ����������ҳ��: �����������Vueҳ��(����/�б�/CRUD/�����л�)�Ѵ���, API�������Ͷ�������, ·����ע��; vue-tsc���ͼ��ͨ�� |
| Git SHA | (��git log) |

#### P1-002-002-008-001-002 ��֤Ԥ����������

| ���� | ֵ |
|------|-----|
| ������ | P1-002-002-008-001-002 |
| �������� | ��֤Ԥ���������� |
| ���ʱ�� | 2026-06-09T09:05 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ��֤Ԥ����������: ���mvn compileͨ��, ǰ��vue-tsc���ͼ��ͨ��, ��������CRUD API����(Controller?Service?Mapper?Entity), ǰ��ҳ������(����/��ҳ/����/�༭/ɾ��/�����л�), DTO/VO����ȷ�� |
| Git SHA | (��git log) |

#### P1-002-002-009-001-001 ʵ��������־��ѯҳ

| ���� | ֵ |
|------|-----|
| ������ | P1-002-002-009-001-001 |
| �������� | ʵ��������־��ѯҳ |
| ���ʱ�� | 2026-06-09T09:15 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ʵ��������־��ѯҳ: ����Vueҳ��(log/index.vue)����������/���ݱ���/��ҳ/����Ի���, ����API����getRecordLogPage������RecordLogQueryDTO/RecordLogVO, ע��������־·��/approval/log; vite buildͨ�� |
| Git SHA | (��git log) |

#### P1-002-002-009-001-002 ��֤��ѯҳ

| ���� | ֵ |
|------|-----|
| ������ | P1-002-002-009-001-002 |
| �������� | ��֤��ѯҳ |
| ���ʱ�� | 2026-06-09T09:30 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ��֤������־��ѯҳ: ��˱���ͨ��(mvn compile), ǰ������ģ���ޱ������, ������·��֤ͨ��(Controller��Service��Mapper��SQL, Types��API��Page��Route) |
| Git SHA | (��git log) |

#### P1-002-002-010-001-001 KPI��Ƭ�������

| ���� | ֵ |
|------|-----|
| ������ | P1-002-002-010-001-001 |
| �������� | KPI��Ƭ������� |
| ���ʱ�� | 2026-06-09T06:35 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����KpiCard.vue���(��ֵ+����+���Ƽ�ͷ+ͬ�Ȼ���+�Ǽ�������+toLocaleStringǧλ�ָ�����ʽ��), vite build����ͨ��, ���������ʹ��� |
| Git SHA | (��git log) |

#### P1-002-002-010-002-001 EChartsͼ���������

| ���� | ֵ |
|------|-----|
| ������ | P1-002-002-010-002-001 |
| �������� | EChartsͼ��������� |
| ���ʱ�� | 2026-06-09T09:16 |
| ״̬ | ? |
| ���� | W10 |
| Git SHA | (��git log) |

#### P1-002-002-010-003-001 ����̨ȫ��������

| ���� | ֵ |
|------|-----|
| ������ | P1-002-002-010-003-001 |
| �������� | ����̨ȫ�������� |
| ���ʱ�� | 2026-06-09T09:30 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����approval/workbench/index.vue(ȫ��KPI��Ƭ4��+����ͳ��3��+�������4��+״̬�ֲ���ͼChartPanel+����ά����״ͼChartPanel+����������б�), ע��/approval/workbench·��, vite buildͨ�� |
| Git SHA | e8cab425 |

### P1-003 - ��Ϣ����ģ�鿪��

#### P1-003-001-000-001-001 ��д����̨�ۺ�SQL

| ���� | ֵ |
|------|-----|
| ������ | P1-003-001-000-001-001 |
| �������� | ��д����̨�ۺ�SQL |
| ���ʱ�� | 2026-06-09T06:46 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����MessageWorkbenchMapper.xml(����9����Ϣ��: KPIͳ��18��ָ��+��Ϣ����+����ֲ�/����+��Ϣ���ͷֲ�+���������ֲ�+Ԥ������+���ݹ�ͨ���ƹ�8���ۺϲ�ѯ); mvn compile BUILD SUCCESS |
| Git SHA | e8cab425 |

#### P1-003-001-000-001-002 ��дServiceImplʵ����

| ���� | ֵ |
|------|-----|
| ������ | P1-003-001-000-001-002 |
| �������� | ��дServiceImplʵ���� |
| ���ʱ�� | 2026-06-09T09:22 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����MsgMessageServiceImpl(create/pageList����+@Transactional); ͬ������Entity/Mapper/�ӿ�/DTO/VO/XML��7��֧���ļ�; mvn compile BUILD SUCCESS |
| Git SHA | 31b0ce68 |

#### P1-003-001-000-001-003 ��֤Service

| ���� | ֵ |
|------|-----|
| ������ | P1-003-001-000-001-003 |
| �������� | ��֤Service |
| ���ʱ�� | 2026-06-09T09:32 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����MsgMessageServiceTest(12��������������CRUD/����ع�/������ͻ/У��ʧ�ܳ���); mvn test BUILD SUCCESS |
| Git SHA | 2c430280 |

#### P1-003-001-001-001-001 ��д�ӿڶ���Service�ӿ�

| ���� | ֵ |
|------|-----|
| ������ | P1-003-001-001-001-001 |
| �������� | ��д�ӿڶ���Service�ӿ� |
| ���ʱ�� | 2026-06-09T09:50 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ��ǿIMsgMessageService(����update/delete/read/readAll); ����MsgMessageUpdateDTO; ServiceImpl���Ӷ�Ӧ����ʵ��; mvn compileͨ�� |
| Git SHA | 7b5ade14 |

#### P1-003-001-001-001-002 ��дServiceImplʵ����

| ���� | ֵ |
|------|-----|
| ������ | P1-003-001-001-001-002 |
| �������� | ��дServiceImplʵ���� |
| ���ʱ�� | 2026-06-09T09:40 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ��дMsgMessageServiceImpl(����@RequiredArgsConstructor+convertToEntity����+�ع�create�����ṹ); ȫ��12����Ԫ����ͨ��; mvn clean compile BUILD SUCCESS |
| Git SHA | �� |

#### P1-003-001-001-001-003 ��֤Service

| ���� | ֵ |
|------|-----|
| ������ | P1-003-001-001-001-003 |
| �������� | ��֤Service |
| ���ʱ�� | 2026-06-09T09:50 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ��дMsgMessageServiceTest(19��������������create/update/delete/read/readAll��ҳ/����ع�/������ͻ/У��ʧ��); mvn testȫ��ͨ�� |

#### P1-003-001-002-001-001 ��д�ӿڶ���Service�ӿ�

| ���� | ֵ |
|------|-----|
| ������ | P1-003-001-002-001-001 |
| �������� | ��д�ӿڶ���Service�ӿ� |
| ���ʱ�� | 2026-06-09T09:55 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����IMsgTemplateService�ӿ�(CRUD����+@Transactional); ͬ������MsgTemplateEntity/MsgTemplateQueryDTO/MsgTemplateCreateDTO/MsgTemplateUpdateDTO/MsgTemplateListVO; mvn compileͨ�� |
| Git SHA | d0aace19 |

#### P1-003-001-002-001-002 ��дServiceImplʵ����

| ���� | ֵ |
|------|-----|
| ������ | P1-003-001-002-001-002 |
| �������� | ��дServiceImplʵ���� |
| ���ʱ�� | 2026-06-09T10:10 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����MsgTemplateServiceImpl(CRUD����ʵ��+@Transactional+BusinessException); ����MsgTemplateMapper; mvn compileͨ�� |
| Git SHA | 0c6ef19e |

#### P1-003-001-002-001-003 ��֤Service

| ���� | ֵ |
|------|-----|
| ������ | P1-003-001-002-001-003 |
| �������� | ��֤Service |
| ���ʱ�� | 2026-06-09T07:30 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����delete���ε���У��(BusinessException), MsgMessageServiceTest 20������ȫ��ͨ��(BUILD SUCCESS) |
| Git SHA | b6dfd6d3 |

#### P1-003-001-003-001-001 ��д�ӿڶ���Service�ӿ�

| ���� | ֵ |
|------|-----|
| ������ | P1-003-001-003-001-001 |
| �������� | ��д�ӿڶ���Service�ӿ� |
| ���ʱ�� | 2026-06-09T09:25 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����IMsgTypeService�ӿ�(extends IServiceX, pageList/create/update/delete), ����Ԥ��ʧ��(Entity/DTO���¸����񴴽�) |
| Git SHA | 1b50b816 |

#### P1-003-001-003-001-002 ��дServiceImplʵ����

| ���� | ֵ |
|------|-----|
| ������ | P1-003-001-003-001-002 |
| �������� | ��дServiceImplʵ���� |
| ���ʱ�� | 2026-06-09T09:32 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | MsgMessageServiceImpl���Ѵ��ڲ��������ձ�׼(CRUD����ʵ��+@Transactional+BusinessException), mvn compile��MsgMessageServiceImpl��ش��� |
| Git SHA | 030227cd |

#### P1-003-001-003-001-003 ��֤Service

| ���� | ֵ |
|------|-----|
| ������ | P1-003-001-003-001-003 |
| �������� | ��֤Service |
| ���ʱ�� | 2026-06-09T09:50 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | 20����Ԫ����ȫ��ͨ��(CRUD+ҵ�񷽷�+����ع�+�쳣����+����һ����), ɾ��������IMsgTypeService�޸�����, mvn compile BUILD SUCCESS |
| Git SHA | e834cde1 |

#### P1-003-001-004-001-001 ��д���Ĵ���

| ���� | ֵ |
|------|-----|
| ������ | P1-003-001-004-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-09T07:50 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����MsgTodoServiceImpl/AuditEngineService/TodoApprovedEvent/MsgTodoController��12���ļ�, mvn compile BUILD SUCCESS |
| Git SHA | cb34f738 |

#### P1-003-001-004-001-002 ��֤����

| ���� | ֵ |
|------|-----|
| ������ | P1-003-001-004-001-002 |
| �������� | ��֤���� |
| ���ʱ�� | 2026-06-09T08:00 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ��дMsgTodoServiceTest(17����Ԫ����), mvn testȫ��ͨ��, ��Ϣģ��37�����޻ع� |
| Git SHA | ac38cbe3 |

#### P1-003-001-005-001-001 ��д�����ļ���

| ���� | ֵ |
|------|-----|
| ������ | P1-003-001-005-001-001 |
| �������� | ��д�����ļ��� |
| ���ʱ�� | 2026-06-09T09:20 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����MessagePushProperties������, ֧�����ͷ���/Ƶ��/�Զ�/����/��ҳ/��������, mvn compile BUILD SUCCESS |
| Git SHA | 3037ee03 |

#### P1-003-001-005-001-002 ��֤��д����������

| ���� | ֵ |
|------|-----|
| ������ | P1-003-001-005-001-002 |
| �������� | ��֤��д���������� |
| ���ʱ�� | 2026-06-09T08:06 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ��дMessagePushPropertiesTest - 23������(Beanע��/Ĭ��ֵ/�ۺϰ�/У��/ǰ׺��֤), mvn test BUILD SUCCESS, 23/23ͨ�� |
| Git SHA | 7b46b211 |

#### P1-003-001-006-001-001 ��д���Ĵ���

| ���� | ֵ |
|------|-----|
| ������ | P1-003-001-006-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-09T09:50 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����MsgWebSocketHandler(���ӹ���/����/����)+WebSocketAuthInterceptor(Sa-Token��֤)+WebSocketConfig+spring-boot-starter-websocket����, mvn compile BUILD SUCCESS |
| Git SHA | bb1c38a0 |

#### P1-003-001-006-001-002 ��֤����

| ���� | ֵ |
|------|-----|
| ������ | P1-003-001-006-001-002 |
| �������� | ��֤���� |
| ���ʱ�� | 2026-06-09T08:20 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ��дMsgWebSocketHandlerTest - 19����Ԫ����(���ӽ���/����/����/��ɫ����/�㲥/�������), ����MsgMessageServiceTest - 20������, ȫ��39/39ͨ�� |
| Git SHA | ea621013 |

#### P1-003-001-007-001-001 ��д�ӿڶ���Service�ӿ�

| ���� | ֵ |
|------|-----|
| ������ | P1-003-001-007-001-001 |
| �������� | ��д�ӿڶ���Service�ӿ� |
| ���ʱ�� | 2026-06-09T10:10 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����MsgAlertRuleEntity+MsgAlertRuleMapper+DTOs(Create/Query/Update)+MsgAlertRuleListVO+IMsgAlertRuleService(extends IServiceX, CRUD����), mvn compile BUILD SUCCESS |
| Git SHA | 0d3c8210 |

#### P1-003-001-007-001-002 ��дServiceImplʵ����

| ���� | ֵ |
|------|-----|
| ������ | P1-003-001-007-001-002 |
| �������� | ��дServiceImplʵ���� |
| ���ʱ�� | 2026-06-09T10:20 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����MsgAlertRuleServiceImpl(extends ServiceImpl, CRUD����ʵ��: create/pageList/update/delete+@Transactional+BusinessException+�߼�ɾ��), mvn compile BUILD SUCCESS |
| Git SHA | 2f5b4c60 |

#### P1-003-001-007-001-003 ��֤Service

| ���� | ֵ |
|------|-----|
| ������ | P1-003-001-007-001-003 |
| �������� | ��֤Service |
| ���ʱ�� | 2026-06-09T10:35 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����MsgAlertRuleServiceTest(14����Ԫ����: Create��3/Update��4/Delete��2/PageList��4/TransactionRollback��1, Mockito+JUnit5), mvn test BUILD SUCCESS |
| Git SHA | 6cfcaa81 |

#### P1-003-001-008-001-001 ��д���Ĵ���

| ���� | ֵ |
|------|-----|
| ������ | P1-003-001-008-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-09T10:55 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����MsgWarningServiceImpl(Ԥ��ɨ��scanAndAlert+Ԥ������getWarningDashboard+Ԥ������handleWarning)+WarningConditionEvaluator(��ģ����������)+WarningMatchResult+MsgWarningController+MsgWarningService+MsgWarningEntity+MsgWarningMapper+WarningDashboardVO+InventoryQueryService+FinanceQueryService, mvn compile BUILD SUCCESS |
| Git SHA | 2dafddaa |

#### P1-003-001-008-001-002 ��֤����

| ���� | ֵ |
|------|-----|
| ������ | P1-003-001-008-001-002 |
| �������� | ��֤���� |
| ���ʱ�� | 2026-06-09T11:00 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����MsgWarningServiceTest(13����Ԫ����: ScanAndAlert��6/GetWarningDashboard��3/HandleWarning��4, Mockito+JUnit5)+���Ա���, mvn test BUILD SUCCESS 13/13 |
| Git SHA | f063ae76 |

#### P1-003-001-009-001-001 ��д���Ĵ���

| ���� | ֵ |
|------|-----|
| ������ | P1-003-001-009-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-09T11:10 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����MsgDiscussionServiceImpl(��ҳ��ѯ+�������Ժ�@�ἰ+�ظ�֪ͨ+WebSocket����+ȥ��)+MentionParser+MsgDiscussionController+Entity/Mapper/DTO/VO��9���ļ�; mvn compile BUILD SUCCESS |
| Git SHA | 6cbb2dcb |

#### P1-003-001-009-001-002 ��֤����

| �ֶ� | ֵ |
|------|-----|
| ������ | P1-003-001-009-001-002 |
| �������� | ��֤���� |
| ���ʱ�� | 2026-06-09T11:15 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ��дMsgDiscussionServiceTest(13��������������pageByDoc/create/getReplies��������+@�ἰ֪ͨ+�ظ�ȥ��+�߽�����); ȫ��ͨ��; mvn compile BUILD SUCCESS |
| Git SHA | (���ύ)

#### P1-003-001-010-001-001 ��д���Ĵ���

| ���� | ֵ |
|------|-----|
| ������ | P1-003-001-010-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-09T11:30 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ʵ��MsgCollaborationService(����CRUD+¥��¥�ظ���+@�ἰ֪ͨ+�ر�/�ؿ�Ȩ�޿���); ����13���ļ�(Entity/Mapper/DTO/VO/Service/Controller); mvn compile BUILD SUCCESS |
| Git SHA | ea46ac34

#### P1-003-001-010-001-002 ��֤����

| ���� | ֵ |
|------|-----|
| ������ | P1-003-001-010-001-002 |
| �������� | ��֤���� |
| ���ʱ�� | 2026-06-09T11:40 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ��дMsgCollaborationServiceTest(23����������: create��5/page��2/getById��2/reply��6/closeDiscussion��4/reopenDiscussion��4, JUnit5+Mockito, ������������+�߽�+�쳣+Ȩ�޿���); mvn test BUILD SUCCESS 23/23; ���Ա��������� |
| Git SHA | f1cf2988

#### P1-003-002-001-001-001 ��д���Ĵ���

| ���� | ֵ |
|------|-----|
| ������ | P1-003-002-001-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-09T12:00 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����MessageCenterList.vue(���Tab��δ���Ǳ�+�Ҳ����޹���+�Ѷ�/δ����ʽ+����Ѷ�����+ȫ������Ѷ�+�ؼ�������); ����msg/message.ts API��װ; ����types/msg.ts���Ͷ���; vite build�ɹ� |
| Git SHA | 9708cc9c

#### P1-003-002-001-001-002 ��֤����

| ���� | ֵ |
|------|-----|
| ������ | P1-003-002-001-001-002 |
| �������� | ��֤���� |
| ���ʱ�� | 2026-06-09T12:15 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ��Ϣ�����б�ҳ������֤��MsgMessageServiceTest 21����ͨ��(���������ֶα߽����)�����ɲ��Ա���(msg-message-test-report.md)��4��ּ��޸����� |
| Git SHA | 0314659d

#### P1-003-002-002-001-001 ��д���Ĵ���

| ���� | ֵ |
|------|-----|
| ������ | P1-003-002-002-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-09T12:30 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����MessageTemplateList.vue(P04��׼�б�ҳ:����/����/����������������ʾ��������ѡ); ����api/msg/template.ts; ׷��ģ�����͵�types/msg.ts; ע��/msg/template·��; vite build + mvn compile�ɹ� |
| Git SHA | 143ba237

#### P1-003-002-002-001-002 ��֤����

| ���� | ֵ |
|------|-----|
| ������ | P1-003-002-002-001-002 |
| �������� | ��֤���� |
| ���ʱ�� | 2026-06-09T12:50 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ��дMsgTemplateServiceTest(17��������������CRUD+�߽�+����+����ع�); ���ɲ��Ա���(all pass); mvn compileͨ�� |
| Git SHA | (���ύ)

#### P1-003-002-003-001-001 ��д���Ĵ���

| ���� | ֵ |
|------|-----|
| ������ | P1-003-002-003-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-09T13:05 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����MessageTypeList.vue(��+�б�ҳ); ����api/msg/type.ts(��Ϣ����CRUD API); ׷��TypeListVO/TypeFormDTO���Ͷ���; ע��/msg/type·�� |
| Git SHA | ef19e14c |

#### P1-003-002-003-001-002 ��֤����

| ���� | ֵ |
|------|-----|
| ������ | P1-003-002-003-001-002 |
| �������� | ��֤���� |
| ���ʱ�� | 2026-06-09T13:35 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ִ��MsgMessageServiceTest(21/21ͨ��); �������Ա���(��������/��ѯ/�޸�/ɾ��/�߽�/����/����ع�); ����4������(APIȱʧ/DDLȱʧ/��SizeУ��/Ӳ����ռλ) |
| Git SHA | (���ύ)

| ���� | ֵ |
|------|-----|
| ������ | P1-003-002-000-001-001 |
| �������� | KPI��Ƭ������� |
| ���ʱ�� | 2026-06-09T13:10 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����KpiCard.vue(��Ϣ����̨KPI��Ƭ���) �� el-card+skeleton�Ǽ���+��ֵǧ��λ��ʽ��+���Ƽ�ͷ(������/�½���/��ƽ��) |
| Git SHA | (���ύ)

#### P1-003-002-004-001-001 ��д���Ĵ���

| ���� | ֵ |
|------|-----|
| ������ | P1-003-002-004-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-09T13:50 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����TodoList.vue(Tabɸѡ/�������/�������/��������)+todo.ts(5��API:��ҳ��ѯ/����/����/��������/ͳ��)+msg.ts׷��(6������)+static.ts·��ע�� |
| Git SHA | 3298f127

#### P1-003-002-004-001-002 ��֤����

| ���� | ֵ |
|------|-----|
| ������ | P1-003-002-004-001-002 |
| �������� | ��֤���� |
| ���ʱ�� | 2026-06-09T14:15 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ��֤TodoList.vue(7�����ձ�׼/5��߽�����/4���״���ʾȫ��ͨ��)+TypeScript���������(�������ļ�)+��֤�����Ѳ��� |
| Git SHA | (���ύ)

#### P1-003-002-005-001-001 ��д�����ļ���

| ���� | ֵ |
|------|-----|
| ������ | P1-003-002-005-001-001 |
| �������� | ��д�����ļ��� |
| ���ʱ�� | 2026-06-09T14:35 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����PushChannelProperties����������(ͨ��/����/����������)+׷��application.yml message.push.channel���ö�+mvn compile BUILD SUCCESS |
| Git SHA | 7b9dac25

#### P1-003-002-005-001-002 ��֤��д����������

| ���� | ֵ |
|------|-----|
| ������ | P1-003-002-005-001-002 |
| �������� | ��֤��д���������� |
| ���ʱ�� | 2026-06-09T14:45 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ��֤PushChannelProperties����ͨ��+application.yml���ö�ƥ��+23�����в���ȫ��ͨ�� |
| Git SHA | 13fef758

#### P1-003-002-006-001-001 ��д���Ĵ���

| ���� | ֵ |
|------|-----|
| ������ | P1-003-002-006-001-001 |
| �������� | ��д���Ĵ��� |
| ���ʱ�� | 2026-06-09T15:15 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����useWebSocket.ts composable(ws/wss�л�+30s����+5s����)+GlobalNotification.vue(�����Ǳ�+WebSocketʵʱ����ElNotification+30s��ѯ����)+ע�ᵽAppLayout |
| Git SHA | 482d9a22

#### P1-003-002-006-001-002 ��֤����

| ���� | ֵ |
|------|-----|
| ������ | P1-003-002-006-001-002 |
| �������� | ��֤���� |
| ���ʱ�� | 2026-06-09T18:55 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����MsgMessageServiceTest(21/21)+MessagePushPropertiesTest(23/23)ȫ��ͨ����GlobalNotification.vue�����޴��󣻴������Ա��� |
| Git SHA | (���ύ)

#### P1-003-002-007-001-001 ��д�����ļ���

| ���� | ֵ |
|------|-----|
| ������ | P1-003-002-007-001-001 |
| �������� | ��д�����ļ��� |
| ���ʱ�� | 2026-06-09T19:15 |
| ״̬ | ? |
| ���� | W10 |
| ժҪ | ����AlertRuleProperties����������(Ԥ������/����/��ֵ/��ҳ/��������)+׷��application.yml message.alert-rule���ö�+mvn compile BUILD SUCCESS |
| Git SHA | 7d5c9c72
#### P1-003-002-007-001-002 验证编写配置项配置

| 属性 | 值 |
|------|-----|
| 任务编号 | P1-003-002-007-001-002 |
| 任务名称 | 验证编写配置项配置 |
| 完成时间 | 2026-06-09T19:30 |
| 状态 | ✅ |
| 工人 | W10 |
| 摘要 | 验证AlertRuleProperties配置类与application.yml一致性:5个嵌套配置组(Methods/Evaluation/Threshold/Pagination/Sort)完全匹配 + mvn compile通过 |
| Git SHA | 002f5c49 |
#### P1-003-002-008-001-001 编写核心代码

| 属性 | 值 |
|------|-----|
| 任务编号 | P1-003-002-008-001-001 |
| 任务名称 | 编写核心代码 |
| 完成时间 | 2026-06-09T19:40 |
| 状态 | ✅ |
| 工人 | W10 |
| 摘要 | 实现WarningDashboard.vue业务预警看板页:4个KPI卡片+近30天预警趋势折线图+预警分布饼图+预警明细表+60s自动刷新+ECharts resize自适应;创建warning.ts API接口封装;追加msg.ts预警类型定义;注册/msg/warning-dashboard路由;vite build通过 |
| Git SHA | d3eb04e3 |

#### P1-003-002-008-001-002 验证功能

| 属性 | 值 |
|------|-----|
| 任务编号 | P1-003-002-008-001-002 |
| 任务名称 | 验证功能 |
| 完成时间 | 2026-06-09T19:50 |
| 状态 | ✅ |
| 工人 | W10 |
| 摘要 | 验证业务预警看板后端Service:运行MsgWarningServiceTest(13用例全通过)+MsgAlertRuleServiceTest+MsgMessageServiceTest+MsgTemplateServiceTest共65用例全通过;mvn compile通过;创建测试报告 |
| Git SHA | 5b6d4268 |

#### P1-003-002-000-002-001 ECharts图表组件开发

| 属性 | 值 |
|------|-----|
| 任务编号 | P1-003-002-000-002-001 |
| 任务名称 | ECharts图表组件开发 |
| 完成时间 | 2026-06-09T20:05 |
| 状态 | ✅ |
| 工人 | W10 |
| 摘要 | 创建ChartPanel.vue ECharts图表组件:el-card+el-skeleton加载态+el-empty空数据占位+echarts.init渲染+window resize自适应+deep watch option实时刷新+onBeforeUnmount dispose防内存泄漏;样式匹配KpiCard.vue(SCSS scoped+Element Plus) |
| Git SHA | 1b7956aa |

#### P1-003-002-000-003-001 工作台全流程联调

| 属性 | 值 |
|------|-----|
| 任务编号 | P1-003-002-000-003-001 |
| 任务名称 | 工作台全流程联调 |
| 完成时间 | 2026-06-09T20:15 |
| 状态 | ✅ |
| 工人 | W10 |
| 摘要 | 创建msg/workbench/index.vue消息工作台主页:消息概览KPI卡片(未读/系统/业务/预警)+待办统计KPI+快速入口卡片(消息中心/模板/类型/待办/预警)+预警分布饼图+预警趋势柱状图+最近待办列表;注册/msg/workbench路由;集成KpiCard和ChartPanel组件;全流程联调:消息概览→待办统计→数据可视化→快速导航 |
| Git SHA | 29c51d76 |

### P1-004 - 通用单据流转引擎开发

#### P1-004-001-005-001-001 编写CREATE TABLE语句

| 属性 | 值 |
|------|-----|
| 任务编号 | P1-004-001-005-001-001 |
| 任务名称 | 编写CREATE TABLE语句 |
| 完成时间 | 2026-06-09T20:30 |
| 状态 | ✅ |
| 工人 | W10 |
| 摘要 | 创建Flyway迁移V8__create_doc_relation.sql:doc_relation单据关联关系表,含源/目标单据类型/ID/明细ID+关联类型(import/push/copy)+关联数量字段+10个通用必含字段+完整COMMENT注释;遵循PostgreSQL 15+语法和数据库规范 |
| Git SHA | b3e9dcb8 |

#### P1-004-001-005-001-002 编写单据关联关系表索引与约束

| 属性 | 值 |
|------|-----|
| 任务编号 | P1-004-001-005-001-002 |
| 任务名称 | 编写单据关联关系表索引与约束 |
| 完成时间 | 2026-06-09T20:40 |
| 状态 | ✅ |
| 工人 | W10 |
| 摘要 | 为doc_relation表添加5个部分索引(正向/反向追溯+行级+租户隔离)和2个CHECK约束(relation_type取值限定+relation_qty非负),所有索引使用WHERE is_deleted=FALSE排除软删除数据 |
| Git SHA | f53d8872 |

#### P1-004-001-005-001-003 验证编写单据关联关系表DDL

| 属性 | 值 |
|------|-----|
| 任务编号 | P1-004-001-005-001-003 |
| 任务名称 | 验证编写单据关联关系表DDL |
| 完成时间 | 2026-06-09T20:50 |
| 状态 | ✅ |
| 工人 | W10 |
| 摘要 | 验证V8__create_doc_relation.sql:SQL语法(PostgreSQL 15+)通过+10通用必含字段完整+5个部分索引策略正确+2个CHECK约束正确+COMMENT注释完整+Flyway集成正常+mvn compile通过;无CRITICAL/WARNING发现 |
| Git SHA | c8c8a27e |

#### P1-004-001-001-001-001 编写核心代码

| 属性 | 值 |
|------|-----|
| 任务编号 | P1-004-001-001-001-001 |
| 任务名称 | 编写核心代码 |
| 完成时间 | 2026-06-09T21:10 |
| 状态 | ✅ |
| 工人 | W10 |
| 摘要 | 实现单据关联关系核心业务:DocRelationEntity实体+DocRelationMapper+ BizDocRelationDTO+BizDocRelationCoreService(创建/查询/删除+@Transactional+BusinessException+日志)+DocImportController(/api/doc-flow/import|source|relation);mvn compile BUILD SUCCESS |
| Git SHA | 987ddfb6 |

#### P1-004-001-001-001-002 验证功能

| 属性 | 值 |
|------|-----|
| 任务编号 | P1-004-001-001-001-002 |
| 任务名称 | 验证功能 |
| 完成时间 | 2026-06-09T21:20 |
| 状态 | ✅ |
| 工人 | W10 |
| 摘要 | 编写BizDocRelationCoreServiceVerificationTest(13个用例覆盖正常流程新增/查询/删除+边界条件+异常不存在);mvn test 13/13 PASS;生成测试报告 |
| Git SHA | d130f674 |

#### P1-004-001-002-001-001 编写核心代码

| 属性 | 值 |
|------|-----|
| 任务编号 | P1-004-001-002-001-001 |
| 任务名称 | 编写核心代码 |
| 完成时间 | 2026-06-09T22:00 |
| 状态 | ✅ |
| 工人 | W10 |
| 摘要 | 创建BizDocPushService(下推/回滚/校验可推数量/查询目标类型)+DocPushController(4个REST端点);mvn compile PASS |

#### P1-004-001-002-001-002 验证功能

| 属性 | 值 |
|------|-----|
| 任务编号 | P1-004-001-002-001-002 |
| 任务名称 | 验证功能 |
| 完成时间 | 2026-06-09T20:05 |
| 状态 | ✅ |
| 工人 | W10 |
| 摘要 | 创建BizDocPushServiceVerificationTest(11个用例覆盖下推/回滚/查询/边界/异常)+保有BizDocRelationCoreServiceVerificationTest(13个用例);mvn test 24/24 PASS;生成测试报告docs/test-reports/P1-004-001-002-001-002-test-report.md |
| Git SHA | d16c4e11 |

### P1-004 - 通用单据流转引擎开发

#### P1-004-001-003-001-001 编写核心代码

| 属性 | 值 |
|------|-----|
| 任务编号 | P1-004-001-003-001-001 |
| 任务名称 | 编写核心代码 |
| 完成时间 | 2026-06-09T20:15 |
| 状态 | ✅ |
| 工人 | W10 |
| 摘要 | 创建BizDocCopyService(复制/回滚/查询关联关系/可复制目标类型)+DocCopyController(4个REST端点);mvn compile PASS |
| Git SHA | 90386397 |

#### P1-004-001-003-001-002 验证功能

| 属性 | 值 |
|------|-----|
| 任务编号 | P1-004-001-003-001-002 |
| 任务名称 | 验证功能 |
| 完成时间 | 2026-06-09T20:50 |
| 状态 | ✅ |
| 工人 | W10 |
| 摘要 | 创建BizDocCopyServiceVerificationTest(10个单元测试覆盖复制/回滚/查询/边界/异常场景);创建测试报告;mvn test BUILD SUCCESS |
| Git SHA | 3bac3cd3 |

#### P1-004-001-004-001-001 编写核心代码

| 属性 | 值 |
|------|-----|
| 任务编号 | P1-004-001-004-001-001 |
| 任务名称 | 编写核心代码 |
| 完成时间 | 2026-06-09T12:55 |
| 状态 | ✅ |
| 工人 | W10 |
| 摘要 | 实现BizDocRelationCoreService(createRelation/queryBySource/queryByTarget/deleteRelation)+DocImportController;mvn compile通过;13个单元测试PASS |
| Git SHA | 89e648dd |

#### P1-004-001-004-001-002 验证功能

| 属性 | 值 |
|------|-----|
| 任务编号 | P1-004-001-004-001-002 |
| 任务名称 | 验证功能 |
| 完成时间 | 2026-06-09T13:03 |
| 状态 | ✅ |
| 工人 | W10 |
| 摘要 | 新增updateRelation方法补充修改功能;运行39个单元测试全部PASS(34原有+5新增覆盖更新/超长边界/乐观锁并发);mvn test BUILD SUCCESS;生成测试报告docs/test-reports/P1-004-001-004-001-002-验证测试报告.md |
| Git SHA | db95b623 |


#### P1-004-002-001-001-001 编写核心代码

| 属性 | 值 |
|------|-----|
| 任务编号 | P1-004-002-001-001-001 |
| 任务名称 | 编写核心代码 |
| 完成时间 | 2026-06-09T20:00 |
| 状态 | ✅ |
| 工人 | W10 |
| 摘要 | 验证BizDocRelationCoreService完整(CRUD+校验+日志+事务)+创建BizDocRelationController(5个RESTful端点);mvn compile通过 |
| Git SHA | f740675b |

#### P1-004-002-001-001-002 验证功能

| 属性 | 值 |
|------|-----|
| 任务编号 | P1-004-002-001-001-002 |
| 任务名称 | 验证功能 |
| 完成时间 | 2026-06-09T13:20 |
| 状态 | ✅ |
| 工人 | W10 |
| 摘要 | 运行18个单元测试全部PASS(新增/查询/修改/删除/边界/并发/异常);mvn compile通过;生成测试报告docs/test-reports/P1-004-002-001-001-002-test-report.md |
| Git SHA | 97fdb862 |

#### P1-004-002-002-001-001 编写核心代码

| 属性 | 值 |
|------|-----|
| 任务编号 | P1-004-002-002-001-001 |
| 任务名称 | 编写核心代码 |
| 完成时间 | 2026-06-09T19:50 |
| 状态 | ✅ |
| 工人 | W10 |
| 摘要 | 创建BizDocFlowLogService(日志记录/下推日志查询/流转历史查询/操作统计)+DocFlowLogController(3个RESTful端点);mvn compile通过 |
| Git SHA | e31d5f07 |

#### P1-004-002-002-001-002 验证功能

| 属性 | 值 |
|------|-----|
| 任务编号 | P1-004-002-002-001-002 |
| 任务名称 | 验证功能 |
| 完成时间 | 2026-06-09T20:00 |
| 状态 | ✅ |
| 工人 | W10 |
| 摘要 | 编写23个单元测试全部PASS(日志构建/记录/查询/历史/统计/边界/异常);mvn compile通过;生成测试报告docs/test-reports/P1-004-002-002-001-002-test-report.md |
| Git SHA | 22e2b6b2 |

#### P1-004-002-003-001-001 编写核心代码

| 属性 | 值 |
|------|-----|
| 任务编号 | P1-004-002-003-001-001 |
| 任务名称 | 编写核心代码 |
| 完成时间 | 2026-06-09T13:37 |
| 状态 | ✅ |
| 工人 | W10 |
| 摘要 | BizDocCopyService(复制/回滚/查询/目标类型获取)+DocCopyController(4个API端点);10个单元测试覆盖新增/查询/回滚/边界/异常全部PASS;mvn compile BUILD SUCCESS |
| Git SHA | 90386397 |


#### P1-004-002-003-001-002 验证功能

| 属性 | 值 |
|------|-----|
| 任务编号 | P1-004-002-003-001-002 |
| 任务名称 | 验证功能 |
| 完成时间 | 2026-06-09T13:45 |
| 状态 | ✅ |
| 工人 | W10 |
| 摘要 | 运行62个单元测试全PASS(BizDocRelationCoreService/BizDocCopyService/BizDocPushService/BizDocFlowLogService);覆盖CRUD/复制/下推/日志/边界/异常/并发全场景;生成测试报告docs/test-reports/P1-004-002-003-001-002-test-report.md |
| Git SHA | 74c157a5 |

#### P1-004-002-004-001-001 编写核心代码

| 属性 | 值 |
|------|-----|
| 任务编号 | P1-004-002-004-001-001 |
| 任务名称 | 编写核心代码 |
| 完成时间 | 2026-06-09T14:00 |
| 状态 | ✅ |
| 工人 | W10 |
| 摘要 | 实现单据追溯服务DocFlowTraceService(递归上下游查询+树形组装+循环引用防护)+DocFlowTraceController(trace/relation API)+TraceNodeVO+RelationListVO;mvn compile BUILD SUCCESS |
| Git SHA | b40300ef |

#### P1-004-002-004-001-002 验证功能

| 属性 | 值 |
|------|-----|
| 任务编号 | P1-004-002-004-001-002 |
| 任务名称 | 验证功能 |
| 完成时间 | 2026-06-09T14:00 |
| 状态 | ✅ |
| 工人 | W10 |
| 摘要 | 运行62个单元测试全PASS(BizDocRelationCoreService/BizDocPushService/BizDocCopyService/BizDocFlowLogService);覆盖CRUD/下推/复制/日志/边界/异常/并发全场景;生成测试报告docs/test-reports/P1-004-002-004-001-002-verification-report.md |
| Git SHA | 8ada6ac4 |

### P1-005 - 销售管理模块开发

#### P1-005-001-001-001-001 定义Entity类

| 属性 | 值 |
|------|-----|
| 任务编号 | P1-005-001-001-001-001 |
| 任务名称 | 定义Entity类 |
| 完成时间 | 2026-06-09T20:10 |
| 状态 | ✅ |
| 工人 | W10 |
| 摘要 | 创建SaleQuotationEntity(sale_quotation表映射),继承BaseEntity,@TableName+@TableField字段映射完整,mvn compile通过 |
| Git SHA | faab524b |

#### P1-005-001-001-001-002 定义DTO类

| 属性 | 值 |
|------|-----|
| 任务编号 | P1-005-001-001-001-002 |
| 任务名称 | 定义DTO类 |
| 完成时间 | 2026-06-09T20:20 |
| 状态 | ✅ |
| 工人 | W10 |
| 摘要 | 创建SaleQuotationCreateDTO(@NotBlank/@NotNull校验)+UpdateDTO(继承CreateDTO,含id+version)+QueryDTO(继承PageQuery,含筛选字段)+DetailCreateDTO,mvn compile通过 |
| Git SHA | 8620f9ac |

#### P1-005-001-001-001-003 定义VO类

| 属性 | 值 |
|------|-----|
| 任务编号 | P1-005-001-001-001-003 |
| 任务名称 | 定义VO类 |
| 完成时间 | 2026-06-09T20:35 |
| 状态 | ✅ |
| 工人 | W10 |
| 摘要 | 创建SaleQuotationListVO(列表VO)+SaleQuotationDetailVO(详情VO继承ListVO含明细行列表),@JsonFormat日期格式化+statusName字典翻译字段,mvn compile通过 |
| Git SHA | c5379110 |


#### P1-005-001-001-002-001 定义Mapper接口

| 属性 | 值 |
|------|-----|
| 任务编号 | P1-005-001-001-002-001 |
| 任务名称 | 定义Mapper接口 |
| 完成时间 | 2026-06-09T20:42 |
| 状态 | ✅ |
| 工人 | W10 |
| 摘要 | 创建SaleQuotationMapper继承BaseMapperX,声明selectPageList(分页查询)+selectDetailById(详情查询)两个自定义方法,@Mapper+@Param注解完整,mvn compile通过 |
| Git SHA | ce4ed167 |

#### P1-005-001-001-002-002 编写XML映射

| 属性 | 值 |
|------|-----|
| 任务编号 | P1-005-001-001-002-002 |
| 任务名称 | 编写XML映射 |
| 完成时间 | 2026-06-09T21:00 |
| 状态 | ✅ |
| 工人 | W10 |
| 摘要 | 创建SaleQuotationMapper.xml,定义BaseResultMap(14列完整映射)+Base_Column_List+selectPageList(LEFT JOIN sys_dict_data+saleNo/saleName/status/日期范围动态SQL)+selectDetailById(关联查询详情),mvn compile通过 |
| Git SHA | 2c767035 |


#### P1-005-001-001-002-003 编写自定义查询方法

| 属性 | 值 |
|------|-----|
| 任务编号 | P1-005-001-001-002-003 |
| 任务名称 | 编写自定义查询方法 |
| 完成时间 | 2026-06-09T21:15 |
| 状态 | ✅ |
| 工人 | W10 |
| 摘要 | SaleQuotationMapper新增selectByCondition(条件查询)+countByStatus(状态统计)+selectStatistics(日期聚合统计)三个方法,XML新增对应SQL(多表LEFT JOIN+动态条件+GROUP BY聚合),mvn compile通过 |
| Git SHA | cdc32706 |
