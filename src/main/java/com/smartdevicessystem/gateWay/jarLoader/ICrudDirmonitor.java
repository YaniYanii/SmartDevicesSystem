package iotInfrustructure.gateWay.jarLoader;

public interface ICrudDirmonitor <ID,T> {
	    public ID create(T record);
	    public T read(ID id );
	    public ID update(ID id );
	    public ID delete(ID id );
}

