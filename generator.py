import random
def write_knapsack_input_to_file(filename, weights, values, capacity):
    with open(filename, 'w') as file:
        file.write(f"Knapsack Capacity: {capacity}\n")
        file.write("Weights: " + ', '.join(map(str, weights)) + "\n")
        file.write("Values: " + ', '.join(map(str, values)) + "\n")

num_items = 100
weights = [random.randint(1, 50) for _ in range(num_items)]
values = [random.randint(1, 500) for _ in range(num_items)]
capacity = 500

filename = "input100v2.txt"
write_knapsack_input_to_file(filename, weights, values, capacity)
print(f"Generated inputs written to '{filename}'.")
