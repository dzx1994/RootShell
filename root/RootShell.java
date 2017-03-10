package com.bitbeyond.xposeddemo.root;

import android.os.Handler;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Sean on 2017/3/9.
 * RootShell
 */

public final class RootShell {
    private static final String TAG = RootShell.class.getSimpleName();
    private static final String HOOK_ROOT_CMD = "echo \"rootOK\"\n";
    private static RootShell mInstance;
    private Handler mHandler;
    private OutputStream mOutput;
    private Process mProcess;
    private boolean mRooted;


    private RootShell() {
        this.mHandler = null;
        this.mRooted = false;
        this.mOutput = null;
        this.mProcess = null;
        requestRoot();
    }

    private RootShell(Handler handler) {
        this();
        this.mHandler = handler;
    }

    public static RootShell open(Handler handler) {
        if (mInstance == null) {
            mInstance = new RootShell(handler);
        } else if (mInstance.mHandler == null) {
            mInstance.mHandler = handler;
        }
        return mInstance;
    }

    public static RootShell open() {
        if (mInstance == null) {
            mInstance = new RootShell();
        }
        return mInstance;
    }

    public void close() {
        if (this.mRooted) {
            execute("exit");
            try {
                if (this.mOutput != null) {
                    this.mOutput.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mInstance = null;
    }

    public void execute(String command) {
        try {
            if (this.mOutput == null) {
                return;
            }
            this.mOutput.write(command.getBytes());
            if (!command.trim().endsWith("\n")) {
                this.mOutput.write("\n".getBytes());
            }
            this.mOutput.flush();
        } catch (IOException e) {
        }
    }

    private void requestRoot() {
        try {
            this.mProcess = Runtime.getRuntime().exec("su \n");
            this.mOutput = this.mProcess.getOutputStream();
            this.mOutput.write(HOOK_ROOT_CMD.getBytes());
            this.mOutput.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
