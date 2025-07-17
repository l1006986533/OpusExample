### 如何添加opus解码器到你的项目中？
1. 下载编译好的[opus.aar](https://github.com/theeasiestway/android-opus-codec/blob/develop/opus.aar)，放到app\libs文件夹下
2. 在你的程序中添加以下代码：
```java
import com.theeasiestway.opus.*;

Opus codec = new Opus();
codec.decoderInit(Constants.SampleRate.Companion._16000(), Constants.Channels.Companion.mono());
byte[] decoded = codec.decode(row, Constants.FrameSize.Companion._320());
```
即可将压缩后的opus数据解码为未压缩的pcm数据