Android-Screen-to-Face-Distance-Measurement
===========================================

#### Be aware that this code is unmaintained since a couple of years and will probably not work with current android versions. 

Calculate the distance between your smartphone screen and your face using the front facing camera. The idea is pretty simple and best explained in an image.

![alt tag](Images/Idea.png)

An object or a "distance" will seem smaller once its further away from the camera than when its closer to it. So one should be able to calculate the distance between camera and face while using the eye distance at a reference length, in our case 29.7cm (the length of an A4 paper), and multiplying the reference length with the change of eye distance.

Close: 
![alt tag](Images/Close.jpg)
Far:
![alt tag](Images/Far.jpg)

For a video  
https://www.youtube.com/watch?v=-6_pGkPKAL4

And the link to the paper with a more thorough analysation can be found at  
http://ieeexplore.ieee.org/xpl/articleDetails.jsp?arnumber=6825217  
or [here](https://github.com/philiiiiiipp/Android-Screen-to-Face-Distance-Measurement/blob/master/A%20new%20context%20-%20Screen%20to%20Face%20distance%201%201.pdf). 
