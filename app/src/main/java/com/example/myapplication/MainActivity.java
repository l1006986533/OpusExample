package com.example.myapplication;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.theeasiestway.opus.*;

import java.io.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Opus codec = new Opus();
        codec.decoderInit(Constants.SampleRate.Companion._16000(), Constants.Channels.Companion.mono());
        ByteArrayOutputStream pcmOut = new ByteArrayOutputStream();
        try {
            InputStream is = getAssets().open("fileContent.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] nums = line.split(",");
                byte[] row = new byte[nums.length];
                for (int i = 0; i < nums.length; i++) {
                    row[i] = (byte) Integer.parseInt(nums[i].trim());
                }
                byte[] decoded = codec.decode(row, Constants.FrameSize.Companion._320());
                if (decoded != null) {
                    pcmOut.write(decoded, 0, decoded.length);
                }
            }
            br.close();
            File wavFile = new File(getExternalFilesDir(null), "output.wav");
            FileOutputStream fos = new FileOutputStream(wavFile);
            writeWavHeader(fos, pcmOut.toByteArray().length, 16000, 1, 16);
            fos.write(pcmOut.toByteArray());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeWavHeader(OutputStream out, int pcmLen, int sampleRate, int channels, int bitsPerSample) throws IOException {
        int byteRate = sampleRate * channels * bitsPerSample / 8;
        int blockAlign = channels * bitsPerSample / 8;
        int dataLen = pcmLen;
        int totalLen = 36 + dataLen;

        DataOutputStream dos = new DataOutputStream(out);
        dos.writeBytes("RIFF");
        dos.writeInt(Integer.reverseBytes(totalLen));
        dos.writeBytes("WAVE");
        dos.writeBytes("fmt ");
        dos.writeInt(Integer.reverseBytes(16)); // Subchunk1Size for PCM
        dos.writeShort(Short.reverseBytes((short)1)); // AudioFormat PCM
        dos.writeShort(Short.reverseBytes((short)channels));
        dos.writeInt(Integer.reverseBytes(sampleRate));
        dos.writeInt(Integer.reverseBytes(byteRate));
        dos.writeShort(Short.reverseBytes((short)blockAlign));
        dos.writeShort(Short.reverseBytes((short)bitsPerSample));
        dos.writeBytes("data");
        dos.writeInt(Integer.reverseBytes(dataLen));
    }
}