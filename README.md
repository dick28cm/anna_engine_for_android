# AnnaEngine anroid版

一个可以边下边播的下载引擎, 不能下载可以tg联系我: @czb123

demo截图
![avatar](https://raw.githubusercontent.com/dick28cm/anna_engine_for_android/main/docs/demo.jpg)  


## 初始化

static AnnaEngine me()   获取实例

void logEnable(boolean value)   允许日志

java.lang.String version()    获取引擎的版本

void preInit(Context ctx, java.lang.String engineName)    预初始化, 最好运行在 Application.onCreate()
内.保证引擎预先初始化.主要就是加载配置之类的活

void tryInit(Context ctx)   初始化引擎, 最好在获取权限以后, 需要最基础的权限是 WRITE_EXTERNAL_STORAGE
READ_EXTERNAL_STORAGE没有则返回错误码

## 添加任务

java.lang.String addEmule(java.lang.String uri)   添加电驴

java.lang.String addMagnet(java.lang.String infoHash, int index)   添加磁力

java.lang.String addMagnet(java.lang.String infoHash, int index, java.lang.String taskName)    添加磁力

java.lang.String addUri(java.lang.String uri, java.util.Map<java.lang.String,java.lang.String>
params)  添加url并返回taskId

## 获取任务

com.anna.engine.DLL.TaskList getCompletedTasks()   获取已经完成的任务

com.anna.engine.DLL.TaskList getDownloadingTasks()   获取正在下载的任务

## 边下边播

java.lang.String getPlayUrl(java.lang.String taskId)    获取配置

java.lang.String getStatusUrl(java.lang.String taskId)  获取下载状态链接

## 解析磁力/种子

void parseMagnet(java.lang.String magnetUrl, com.anna.engine.MagnetCallback callback)   解析磁力 (异步)

void parseTorrent(java.lang.String path, com.anna.engine.MagnetCallback callback)   解析种子 (异步)

## 任务控制

int pauseDownload(java.lang.String taskId)   暂停

int removeDownload(java.lang.String taskId, boolean delFile)   删除下载

int resumeDownload(java.lang.String taskId)   开始/恢复下载

## 配置

"engine.download.path" = (String)文件下载保存的路径,默认位置位置:/sdcard(外部储存器)/Android/data/(app包名)
/files/AnneEngine(一般为init设置的engineName的值,不传默认为AnnaEngine)/download/ 必须保证路径的可读写权限,否则会导致app闪退     
"engine.resume.path" = (String)任务配置和断点续传保存的路径,默认位置位置:/sdcard(外部储存器)/Android/data/(app包名)
/files/AnneEngine(一般为init设置的engineName的值,不传默认为AnnaEngine)/task/   
"engine.task.max" = (int)同时下载的最大任务数,默认为3.请勿设置太多.否则会导致手机发烫或者性能下降        
"energy.saving.mode" = (bool)省电模式,默认为false,设置省电模式,适用于机顶盒与配置低下的手机    
其他参数暂不公开

java.lang.String getOption(java.lang.String key)   获取配置(String)

boolean getBoolOption(java.lang.String key)  获取配置(bool)

int getIntOption(java.lang.String key)      获取配置(int)

void setOption(java.lang.String key, java.lang.String value)   设置配置(String)

void setBoolOption(java.lang.String key, boolean value)    设置配置(bool)

void setIntOption(java.lang.String key, int value)   设置配置(int)

## 修改任务

void updateTaskName(java.lang.String taskId, java.lang.String taskName)   修改任务名

## 校验返回码

ndk内不向java抛出任务异常,可能会导致闪退,所以用错误码代替返回 关于返回错误码定义
> 0 代表成功    
> -10: 解析磁力/种子出错     
> -20: 序列化磁力/种子数据出错    
> -30: 任务已存在    
> -31: 引擎已经初始化    
> -311: 获取包名失败    
> -312: 没有外部储存器读权限     
> -313: 没有外部储存器写权限     
> -314: 获取外部储存器目录失败     
> -315: 获取签名失败     
> -316: 发生初始化异常     
> -40: 运行任务上限     
> -100: 发生运行时异常       
void	validateRetCode(int ret)   



