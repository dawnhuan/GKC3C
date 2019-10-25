# 工科创的程序
新建了类CarControl用于小车控制，并实现了方向键控制小车。CarControl的使用方法为：新建一个对象，如命名为control, 初始化时传入MainActivity.client，
用control.go()等即可控制小车。go前进，back后退，left,right原地转向。不带参数的go()会让小车一直前进直到stop(), 带参数的go(double time)让小车前进一段时间，其余几个同理。
其中time单位为秒，angle单位为角度。
