import os

os.environ["CUDA_VISIBLE_DEVICES"] = "-1"
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '3'
# import tensorflow as tf
import sys

stderr = sys.stderr
sys.stderr = open(os.devnull, 'w')
import keras

sys.stderr = stderr
import time
import threading

import numpy as np
from keras.preprocessing import image
from keras.models import load_model
from keras import models
import PIL


def normalize(data):
    return np.interp(data, [0, 255], [-1, 1])


N_Objects = 21
Objects = {0: "Apple", 1: "Banana", 2: "Grape", 3: "Pineapple", 4: "Cake", 5: "Candle", 6: "Car", 7: "Fork",
           8: "Eiffel", 9: "Airplane", 10: "Axe", 11: "Bed", 12: "Bread", 13: "Cat", 14: "Hat", 15: "Knife",
           16: "Diamond", 17: "Eye", 18: "Door", 19: "Mountain", 20: "Star"}

model = load_model('C:\\Users\\Elnur\\IdeaProjects\\cs353\\ML1\\model.h5')
print("loaded")


while (1 == 1):
    time.sleep(0.1)
    if os.path.isfile('C:\\Users\\Elnur\\IdeaProjects\\cs353\\ML1\\image.png'):
        try:
            img_path = 'C:\\Users\\Elnur\\IdeaProjects\\cs353\\ML1\\image.png'
            try:
                img = image.load_img(img_path, target_size=(28, 28), color_mode='grayscale')
            except:
                File_object = open(r"C:\Users\Elnur\IdeaProjects\cs353\ML1\problems.txt", "a+")
                File_object.write("Cant load image")
                File_object.close()
                print("Cant load image")
                pass
            try:
                toTest = []
                imgAsArray = image.img_to_array(img)
                imgAsArray = np.reshape(imgAsArray, (28, 28, 1))
                toTest.append(imgAsArray)
                # print(toTest)
                toTest = normalize(toTest)
                resultOfPrediciton = model.predict(np.array(toTest))
            except:
                File_object = open(r"C:\Users\Elnur\IdeaProjects\cs353\ML1\problems.txt", "a+")
                File_object.write("Calculations error")
                File_object.close()
                print("Calculations error")
                pass

            try:
                File_object = open(r"C:\Users\Elnur\IdeaProjects\cs353\ML1\probs.txt", "w+")
                for i in range(0, 21):
                    # print(str(Objects[i]) + "   "+str(resultOfPrediciton[0][i]) )
                    File_object.write(str(Objects[i]) + " " + str(resultOfPrediciton[0][i]) + "\n")
                File_object.close()
            except:
              File_object = open(r"C:\Users\Elnur\IdeaProjects\cs353\ML1\problems.txt", "a+")
              File_object.write("Probs error")
              File_object.close()
              print("Probs error")
              pass

            #print(Objects[np.argmax(resultOfPrediciton[0])])
            # print(np.array(toTest))
            try:
              File_object = open(r"C:\Users\Elnur\IdeaProjects\cs353\ML1\result.txt", "w+")
              File_object.write(Objects[np.argmax(resultOfPrediciton[0])])
              File_object.close()
            except:
              File_object = open(r"C:\Users\Elnur\IdeaProjects\cs353\ML1\problems.txt", "a+")
              File_object.write("Result error")
              File_object.close()
              print("Result error")
              pass


            try:
              maxsize = (80, 80)
              layer_outputs = [layer.output for layer in model.layers[:6]]
              activation_model = models.Model(inputs=model.input, outputs=layer_outputs)
              activations = activation_model.predict(toTest)
              first_layer_activation = activations[0]
              for i in range(32):
                  k = first_layer_activation[0, :, :, i].copy()
                  k = k.reshape(26, 26, 1)
                  img = image.array_to_img(k)
                  img = img.resize(maxsize, PIL.Image.ANTIALIAS)
                  img.save("images\\img" + str(i) + ".png")
            except:
              File_object = open(r"C:\Users\Elnur\IdeaProjects\cs353\ML1\problems.txt", "a+")
              File_object.write("IMage error")
              File_object.close()
              print("IMage error")
              pass
        except:
            File_object = open(r"C:\Users\Elnur\IdeaProjects\cs353\ML1\problems.txt", "a+")
            File_object.write("An exception occurred")
            File_object.close()
            print("An exception occurred")
            pass
    else:
        print("File not exist")
