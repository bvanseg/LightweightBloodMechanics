package com.asx.lbm.api;

@FunctionalInterface
public interface IBleedable
{
    public default int getBloodColor()
    {
        return 0xAAFF0000;
    }
    
    public boolean doesBloodGlow();
}
