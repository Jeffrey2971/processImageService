import hashlib
import requests

image_path = 'image file path'
public_key = 'your 32-bit length public key'
private_key = 'your 128-bit length private key'
salt = '8-bit length random number'

with open(file=image_path, mode='rb') as image_file:
    image_md5 = hashlib.md5(image_file.read()).hexdigest()

sign: str = hashlib.md5((public_key + image_md5 + str(salt) + private_key).encode('utf-8')).hexdigest()

response = requests.post(
    url='http://localhost/single-upload',
    data={
        'demo_image_md5': image_md5,
        'demo_public_key': public_key,
        'demo_salt': salt,
        # 'sync': 'true',
        # 'callback': 'http://127.0.0.1:8080/callback'
    },
    files={
        "uploadFile": open(file=image_path, mode='rb')
    },
    headers={
        'x-jeffrey-api-sign': sign
    }
).json()

print(response)
