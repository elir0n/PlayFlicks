import socket
import sys
import time

time.sleep(10)

if len(sys.argv) != 3:
    sys.exit(1)  # Exit silently if the wrong number of arguments are provided


def sendData():
    server_ip = sys.argv[1]
    port = int(sys.argv[2])

    try:
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.connect((server_ip, port))
        print(f"Connected to server at {server_ip}:{port}\n")

        sys.stdin.flush()
        while True:
            # Prompt the user for input
            user_input = input("").strip()
            print(user_input)

            # Send the user input to the server
            s.sendall((user_input + "\n").encode('utf-8'))

            # Receive the server's response
            response = s.recv(4096).decode('utf-8')
            if not response:
                sendData()
                break
            print(f"Server response:\n{response}\n")

    except (socket.gaierror, socket.error, ConnectionResetError) as e:
        print(f"Socket error: {e}. Retrying in 5 seconds...")
        sendData()
        time.sleep(5)
    except KeyboardInterrupt:
        s.close()
        print("User interrupted. Exiting.")
        sys.exit(0)


sendData()
