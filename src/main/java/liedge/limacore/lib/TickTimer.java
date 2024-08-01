package liedge.limacore.lib;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import static liedge.limacore.util.LimaMathUtil.divideFloat;
import static liedge.limacore.util.LimaMathUtil.divideFloatLerp;

public class TickTimer
{
    private int duration = 1;
    private int currentTick;
    private int previousTick;

    private State timerState = State.STOPPED;

    private @Nullable Runnable onStart;
    private @Nullable BooleanConsumer onStopped;

    public TickTimer() {}

    public void setStartCallback(@Nullable Runnable onStart)
    {
        this.onStart = onStart;
    }

    public void setOnStoppedCallback(@Nullable BooleanConsumer onStopped)
    {
        this.onStopped = onStopped;
    }

    public void startTimer(int duration)
    {
        startTimer(duration, false);
    }

    public void startTimer(int duration, boolean pauseOnStart)
    {
        if (duration > 0)
        {
            this.duration = duration;
            currentTick = 0;
            previousTick = 0;

            if (onStart != null) onStart.run();

            timerState = pauseOnStart ? State.PAUSED : State.RUNNING;
        }
    }

    public void stopTimer()
    {
        if (timerState != State.STOPPED)
        {
            this.currentTick = duration;
            this.previousTick = duration;
            timerState = State.STOPPED;

            if (onStopped != null) onStopped.accept(false);
        }
    }

    public void setPaused(boolean paused)
    {
        if (paused && timerState == State.RUNNING)
        {
            timerState = State.PAUSED;
        }
        else if (timerState == State.PAUSED && !paused)
        {
            timerState = State.RUNNING;
        }
    }

    public State getTimerState()
    {
        return timerState;
    }

    public void tickTimer()
    {
        if (timerState == State.RUNNING)
        {
            if (currentTick < duration)
            {
                previousTick = currentTick;
                currentTick++;
            }
            else if (currentTick == duration && previousTick < duration)
            {
                previousTick = duration;
                timerState = State.STOPPED;
                if (onStopped != null) onStopped.accept(true);
            }
        }
    }

    // Client methods
    public float getProgressPercent()
    {
        return divideFloat(currentTick, duration);
    }

    public boolean isRunningClient()
    {
        return timerState == State.RUNNING && previousTick < duration;
    }

    public float lerpTick(float partialTick)
    {
        return Mth.lerp(partialTick, previousTick, currentTick);
    }

    public float lerpProgressNotPaused(float partialTick)
    {
        if (isRunningClient())
        {
            return divideFloatLerp(partialTick, previousTick, currentTick, duration);
        }
        else
        {
            return 0f;
        }
    }

    public float lerpPausedProgress(float partialTick)
    {
        if (isRunningClient())
        {
            return divideFloatLerp(partialTick, previousTick, currentTick, duration);
        }
        else
        {
            return divideFloat(currentTick, duration);
        }
    }

    public enum State
    {
        RUNNING,
        STOPPED,
        PAUSED
    }
}