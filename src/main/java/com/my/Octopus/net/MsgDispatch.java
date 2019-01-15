package com.my.Octopus.net;

/**
 * Created by davidqian on 17/7/16.
 */

public abstract class MsgDispatch {
    public abstract void dispatch(Session session, byte[] req);

    public abstract void dispatch(int err, byte[] req);

    public abstract void octopusDispatch(Session session, int code);
}
