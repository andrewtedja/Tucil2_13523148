import math
inputX = int(input("Enter int: "));
target = int(input("Enter target: "));
delta = inputX / 2;
print("You entered:", inputX)
print("You entered:", target)

while (math.fabs(inputX - target) > 2):
    prevInput = inputX;
    inputX += delta;

    if ((prevInput < target and inputX > target) or (prevInput > target and inputX < target)):
        delta *= -0.5

    print("prevInput: {}, inputX: {}, delta: {}, target: {}".format(prevInput, inputX, delta, target))
    if (math.fabs(inputX - target) <= 2):
        break;

print("FOUND: ", inputX, target)