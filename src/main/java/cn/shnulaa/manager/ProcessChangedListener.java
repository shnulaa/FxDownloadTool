package cn.shnulaa.manager;

public interface ProcessChangedListener {

	void change(double rate, long speed, Thread t);

}
