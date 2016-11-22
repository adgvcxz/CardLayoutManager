# CardLayoutManager

##简单、可扩展的一个LayoutManager，实现了类似于探探和Tinder，滑动图片

* 监听滑动中的事件与动画过程
* 支持``smoothScrollToPosition``和``scrollToPosition``
* 自定义``LinearLayout.HORIZONTAL``或者``LinearLayout.HORIZONTAL``
* 自定义显示卡片个数以及卡片之间的间隙
* 对滑动过程进行控制
* 简单易用，扩展性高

##预览

``滑动`` 和 ``smoothScrollToPosition``

![](https://github.com/adgvcxz/CardLayoutManager/blob/master/img/card1.gif)
![](https://github.com/adgvcxz/CardLayoutManager/blob/master/img/card2.gif)

##初始化
```java
CardLayoutManager layoutManager = new CardLayoutManager();
recyclerView.setLayoutManager(layoutManager);
new CardSnapHelper().attachToRecyclerView(recyclerView);
```
##监听滑动事件
```java
layoutManager.setOnCardSwipeListener(new OnCardSwipeListener() {
	......
}
```
##自定义滑动控制
如果只是在``smoothScrollToPosition``不想随机卡片的起点和终点可以继承``BaseCardSwipeController``重写其中的方法

如果想完全控制整个滑动，则需要继承``CardSwipeController``实现其中所有方法

## 待开发
* 优化``smoothScrollToPosition``
* 实现``CardItemAnimator``

## 感谢

[ChipsLayoutManager](https://github.com/BelooS/ChipsLayoutManager)

[FanLayoutManager](https://github.com/Cleveroad/FanLayoutManager)



## LICENSE

    Copyright 2016 adgvcxz

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

