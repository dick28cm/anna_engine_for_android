# AnnaEngine anroid版
一个可以边下边播的下载引擎

方法详细资料
me
public static AnnaEngine me()
获取实例
返回:
preInit
public void preInit(Context ctx,
                    java.lang.String engineName)
预初始化, 最好运行在 Application.onCreate() 内.保证引擎预先初始化.主要就是加载配置之类的活
参数:
ctx -
engineName - 引擎名称.也就是配置文件保存的文件夹名.可以自定义, 也可以为空.为空默认就是 AnnaEngine
tryInit
public void tryInit(Context ctx)
             throws com.anna.engine.EngineException
初始化引擎, 最好在获取权限以后, 需要最基础的权限是 WRITE_EXTERNAL_STORAGE READ_EXTERNAL_STORAGE没有则返回错误码
参数:
ctx -
抛出:
com.anna.engine.EngineException - 抛出错误
version
public java.lang.String version()
获取引擎的版本
返回:
parseMagnet
public void parseMagnet(java.lang.String magnetUrl,
                        com.anna.engine.MagnetCallback callback)
解析磁力 (异步)
参数:
magnetUrl -
callback - 回调是异步执行, code<=0则报错
parseTorrent
public void parseTorrent(java.lang.String path,
                         com.anna.engine.MagnetCallback callback)
解析种子 (异步)
参数:
path -
callback - 回调是异步执行, code<=0则报错
addUri
public java.lang.String addUri(java.lang.String uri,
                               java.util.Map<java.lang.String,java.lang.String> params)
                        throws com.anna.engine.EngineException,
                               java.net.MalformedURLException
添加url并返回taskId
参数:
uri - 下载连接
params - 载参数,比如添加头和cookies
返回:
抛出:
EngineException,MalformedURLException - 抛出错误
com.anna.engine.EngineException
java.net.MalformedURLException
addEmule
public java.lang.String addEmule(java.lang.String uri)
                          throws com.anna.engine.EngineException
添加电驴
参数:
uri - 下载连接
返回:
抛出:
com.anna.engine.EngineException - 抛出错误
addMagnet
public java.lang.String addMagnet(java.lang.String infoHash,
                                  int index)
                           throws com.anna.engine.EngineException
添加磁力
参数:
infoHash - 磁力的infohash
index - 文件索引
返回:
抛出:
com.anna.engine.EngineException - 抛出错误
addMagnet
public java.lang.String addMagnet(java.lang.String infoHash,
                                  int index,
                                  java.lang.String taskName)
                           throws com.anna.engine.EngineException
添加磁力
参数:
infoHash - 磁力的infohash
index - 文件索引
taskName - 任务名称
返回:
抛出:
com.anna.engine.EngineException - 抛出错误
resumeDownload
public int resumeDownload(java.lang.String taskId)
开始/恢复下载
参数:
taskId -
返回:
pauseDownload
public int pauseDownload(java.lang.String taskId)
暂停
参数:
taskId -
返回:
removeDownload
public int removeDownload(java.lang.String taskId,
                          boolean delFile)
删除下载
参数:
taskId -
delFile - 是否删除文件,默认为true
返回:
getPlayUrl
public java.lang.String getPlayUrl(java.lang.String taskId)
                            throws com.anna.engine.EngineException
获取播放链接
参数:
taskId -
返回:
抛出:
com.anna.engine.EngineException - 抛出错误
getStatusUrl
public java.lang.String getStatusUrl(java.lang.String taskId)
                              throws com.anna.engine.EngineException
获取下载状态链接
参数:
taskId -
返回:
抛出:
com.anna.engine.EngineException - 抛出错误
getDownloadingTasks
public com.anna.engine.DLL.TaskList getDownloadingTasks()
                                                 throws com.anna.engine.EngineException
获取正在下载的任务
返回:
抛出:
com.anna.engine.EngineException - 抛出错误
getCompletedTasks
public com.anna.engine.DLL.TaskList getCompletedTasks()
                                               throws com.anna.engine.EngineException
获取已经完成的任务
返回:
抛出:
com.anna.engine.EngineException - 抛出错误
updateTaskName
public void updateTaskName(java.lang.String taskId,
                           java.lang.String taskName)
修改任务名
参数:
taskId -
taskName - 任务名
getOption
public java.lang.String getOption(java.lang.String key)
获取配置
参数:
key -
返回:
setOption
public void setOption(java.lang.String key,
                      java.lang.String value)
设置配置
参数:
key -
value -
getIntOption
public int getIntOption(java.lang.String key)
获取配置
参数:
key -
返回:
setIntOption
public void setIntOption(java.lang.String key,
                         int value)
设置配置
参数:
key -
value -
getBoolOption
public boolean getBoolOption(java.lang.String key)
获取配置
参数:
key -
返回:
setBoolOption
public void setBoolOption(java.lang.String key,
                          boolean value)
设置配置
参数:
key -
value -
logEnable
public void logEnable(boolean value)
允许日志
参数:
value -
validateRetCode
public void validateRetCode(int ret)
                     throws com.anna.engine.EngineException
ndk内不向java抛出任务异常,可能会导致闪退,所以用错误码代替返回 关于返回错误码定义 >0 代表成功 -10: 解析磁力/种子出错 -20: 序列化磁力/种子数据出错 -30: 任务已存在 -31: 引擎已经初始化 -311: 获取包名失败 -312: 没有外部储存器读权限 -313: 没有外部储存器写权限 -314: 获取外部储存器目录失败 -315: 获取签名失败 -316: 发生初始化异常 -40: 运行任务上限 -100: 发生运行时异常
参数:
ret -
抛出:
com.anna.engine.EngineException
