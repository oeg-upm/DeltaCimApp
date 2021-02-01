from flask import Flask, Response, request
app = Flask(__name__)

@app.route('/experiment1',methods = ['POST'])
def experiment1():
    response = Response()
    request_path = request.path
    print("\n----- Request POST received ----->\n")
    content_len = int(request.headers.get('Content-Length'))
    post_body = request.data
    print(post_body)
    print(request_path)
    print("<----- Request End -----\n")  
    response.headers['Content-Type'] = 'application/json'
    data = "{\"key\":\"Error\"}"
    if response.status_code == 200:
        data = "{\"key\":\"Ok\"}"
    response.text = data.encode(encoding='utf_8')
    return (response.text, response.status_code, response.headers.items())
      
@app.route('/experiment2',methods = ['POST'])
def experiment2():
    response = Response()
    request_path = request.path
    print("\n----- Request POST received ----->\n")
    content_len = int(request.headers.get('Content-Length'))
    post_body = request.data
    print(post_body)
    print(request_path)
    print("<----- Request End -----\n")  
    response.headers['Content-Type'] = 'application/json'
    data = "{\"key\":\"Error\"}"
    if response.status_code == 200:
        data = "{\"key\":\"Ok\"}"
    response.text = data.encode(encoding='utf_8')
    return (response.text, response.status_code, response.headers.items())
      
@app.route('/experiment3',methods = ['GET'])
def experiment3():
    response = Response()
    request_path = request.path
    print("\n----- Request Start ----->\n")
    print(request_path)
    print(request.headers)
    print("<----- Request End -----\n")
    
    response.headers['Content-Type'] = 'application/xml'
    #request.end_headers()
    data = ""
    with open('oardUpdate.xml', 'r') as file:
        data = file.read().replace('\n', '')
        file.close()
    response.text = data.encode(encoding='utf_8')
    #request.wfile.write(data.encode(encoding='utf_8')) 
    return (response.text, response.status_code, response.headers.items())
    
@app.route('/baselinePOST',methods = ['POST'])
def baselinePOST():
    response = Response()
    request_path = request.path
    print("\n----- Request POST received ----->\n")
    content_len = int(request.headers.get('Content-Length'))
    post_body = request.data
    print(post_body)
    print(request_path)
    print("<----- Request End -----\n")  
    response.headers['Content-Type'] = 'application/json'
    data = "{\"key\":\"Error\"}"
    if response.status_code == 200:
        data = "{\"key\":\"Ok\"}"
    response.text = data.encode(encoding='utf_8')
    return (response.text, response.status_code, response.headers.items())
    
@app.route('/baselineGET',methods = ['GET'])
def baselineGET():
    response = Response()
    request_path = request.path
    print("\n----- Request Start ----->\n")
    print(request_path)
    print(request.headers)
    print("<----- Request End -----\n")
    response.headers['Content-Type'] = 'application/xml'
    data = ""
    with open('oardUpdate.jsonld', 'r') as file:
        data = file.read().replace('\n', '')
        file.close()
    response.text = data.encode(encoding='utf_8')
    return (response.text, response.status_code, response.headers.items())
    
@app.route('/increasing500',methods = ['GET'])
def increasing500():
    response = Response()
    request_path = request.path
    print("\n----- Request Start ----->\n")
    print(request_path)
    print(request.headers)
    print("<----- Request End -----\n")
    response.headers['Content-Type'] = 'application/xml'
    data = ""
    with open('jsonincrement/json553.jsonld', 'r') as file:
        data = file.read().replace('\n', '')
        file.close()
    response.text = data.encode(encoding='utf_8')
    return (response.text, response.status_code, response.headers.items())
    
    
@app.route('/increasing1000',methods = ['GET'])
def increasing1000():
    response = Response()
    request_path = request.path
    print("\n----- Request Start ----->\n")
    print(request_path)
    print(request.headers)
    print("<----- Request End -----\n")
    response.headers['Content-Type'] = 'application/xml'
    data = ""
    with open('jsonincrement/json1106.jsonld', 'r') as file:
        data = file.read().replace('\n', '')
        file.close()
    response.text = data.encode(encoding='utf_8')
    return (response.text, response.status_code, response.headers.items())
    
@app.route('/increasing1500',methods = ['GET'])
def increasing1500():
    response = Response()
    request_path = request.path
    print("\n----- Request Start ----->\n")
    print(request_path)
    print(request.headers)
    print("<----- Request End -----\n")
    response.headers['Content-Type'] = 'application/xml'
    data = ""
    with open('jsonincrement/json1657.jsonld', 'r') as file:
        data = file.read().replace('\n', '')
        file.close()
    response.text = data.encode(encoding='utf_8')
    return (response.text, response.status_code, response.headers.items())
    
    
@app.route('/increasing2000',methods = ['GET'])
def increasing2000():
    response = Response()
    request_path = request.path
    print("\n----- Request Start ----->\n")
    print(request_path)
    print(request.headers)
    print("<----- Request End -----\n")
    response.headers['Content-Type'] = 'application/xml'
    data = ""
    with open('jsonincrement/json2208.jsonld', 'r') as file:
        data = file.read().replace('\n', '')
        file.close()
    response.text = data.encode(encoding='utf_8')
    return (response.text, response.status_code, response.headers.items())


@app.route('/increasing2500',methods = ['GET'])
def increasing2500():
    response = Response()
    request_path = request.path
    print("\n----- Request Start ----->\n")
    print(request_path)
    print(request.headers)
    print("<----- Request End -----\n")
    response.headers['Content-Type'] = 'application/xml'
    data = ""
    with open('jsonincrement/json2760.jsonld', 'r') as file:
        data = file.read().replace('\n', '')
        file.close()
    response.text = data.encode(encoding='utf_8')
    return (response.text, response.status_code, response.headers.items())
    
@app.route('/increasing3000',methods = ['GET'])
def increasing3000():
    response = Response()
    request_path = request.path
    print("\n----- Request Start ----->\n")
    print(request_path)
    print(request.headers)
    print("<----- Request End -----\n")
    response.headers['Content-Type'] = 'application/xml'
    data = ""
    with open('jsonincrement/json3311.jsonld', 'r') as file:
        data = file.read().replace('\n', '')
        file.close()
    response.text = data.encode(encoding='utf_8')
    return (response.text, response.status_code, response.headers.items())

@app.route('/increasing3500',methods = ['GET'])
def increasing3500():
    response = Response()
    request_path = request.path
    print("\n----- Request Start ----->\n")
    print(request_path)
    print(request.headers)
    print("<----- Request End -----\n")
    response.headers['Content-Type'] = 'application/xml'
    data = ""
    with open('jsonincrement/json3862.jsonld', 'r') as file:
        data = file.read().replace('\n', '')
        file.close()
    response.text = data.encode(encoding='utf_8')
    return (response.text, response.status_code, response.headers.items())
    
    
@app.route('/increasing4000',methods = ['GET'])
def increasing4000():
    response = Response()
    request_path = request.path
    print("\n----- Request Start ----->\n")
    print(request_path)
    print(request.headers)
    print("<----- Request End -----\n")
    response.headers['Content-Type'] = 'application/xml'
    data = ""
    with open('jsonincrement/json4413.jsonld', 'r') as file:
        data = file.read().replace('\n', '')
        file.close()
    response.text = data.encode(encoding='utf_8')
    return (response.text, response.status_code, response.headers.items())   

@app.route('/increasing4500',methods = ['GET'])
def increasing4500():
    response = Response()
    request_path = request.path
    print("\n----- Request Start ----->\n")
    print(request_path)
    print(request.headers)
    print("<----- Request End -----\n")
    response.headers['Content-Type'] = 'application/xml'
    data = ""
    with open('jsonincrement/json4964.jsonld', 'r') as file:
        data = file.read().replace('\n', '')
        file.close()
    response.text = data.encode(encoding='utf_8')
    return (response.text, response.status_code, response.headers.items())
    
    
@app.route('/increasingNew5000',methods = ['GET'])
def increasingNew5000():
    response = Response()
    request_path = request.path
    print("\n----- Request Start ----->\n")
    print(request_path)
    print(request.headers)
    print("<----- Request End -----\n")
    response.headers['Content-Type'] = 'application/xml'
    data = ""
    with open('jsonincrement/json5515.jsonld', 'r') as file:
        data = file.read().replace('\n', '')
        file.close()
    response.text = data.encode(encoding='utf_8')
    return (response.text, response.status_code, response.headers.items())    
    
@app.route('/increasing5500',methods = ['GET'])
def increasing5500():
    response = Response()
    request_path = request.path
    print("\n----- Request Start ----->\n")
    print(request_path)
    print(request.headers)
    print("<----- Request End -----\n")
    response.headers['Content-Type'] = 'application/xml'
    data = ""
    with open('jsonincrement/json6066.jsonld', 'r') as file:
        data = file.read().replace('\n', '')
        file.close()
    response.text = data.encode(encoding='utf_8')
    return (response.text, response.status_code, response.headers.items())
    
    
@app.route('/increasing6000',methods = ['GET'])
def increasing6000():
    response = Response()
    request_path = request.path
    print("\n----- Request Start ----->\n")
    print(request_path)
    print(request.headers)
    print("<----- Request End -----\n")
    response.headers['Content-Type'] = 'application/xml'
    data = ""
    with open('jsonincrement/json6617.jsonld', 'r') as file:
        data = file.read().replace('\n', '')
        file.close()
    response.text = data.encode(encoding='utf_8')
    return (response.text, response.status_code, response.headers.items())
    
@app.route('/increasing6500',methods = ['GET'])
def increasing6500():
    response = Response()
    request_path = request.path
    print("\n----- Request Start ----->\n")
    print(request_path)
    print(request.headers)
    print("<----- Request End -----\n")
    response.headers['Content-Type'] = 'application/xml'
    data = ""
    with open('jsonincrement/json7169.jsonld', 'r') as file:
        data = file.read().replace('\n', '')
        file.close()
    response.text = data.encode(encoding='utf_8')
    return (response.text, response.status_code, response.headers.items())
    
    
@app.route('/increasing7000',methods = ['GET'])
def increasing7000():
    response = Response()
    request_path = request.path
    print("\n----- Request Start ----->\n")
    print(request_path)
    print(request.headers)
    print("<----- Request End -----\n")
    response.headers['Content-Type'] = 'application/xml'
    data = ""
    with open('jsonincrement/json7720.jsonld', 'r') as file:
        data = file.read().replace('\n', '')
        file.close()
    response.text = data.encode(encoding='utf_8')
    return (response.text, response.status_code, response.headers.items())
    
@app.route('/increasing7500',methods = ['GET'])
def increasing7500():
    response = Response()
    request_path = request.path
    print("\n----- Request Start ----->\n")
    print(request_path)
    print(request.headers)
    print("<----- Request End -----\n")
    response.headers['Content-Type'] = 'application/xml'
    data = ""
    with open('jsonincrement/json8271.jsonld', 'r') as file:
        data = file.read().replace('\n', '')
        file.close()
    response.text = data.encode(encoding='utf_8')
    return (response.text, response.status_code, response.headers.items())
    
    
@app.route('/increasing8000',methods = ['GET'])
def increasing8000():
    response = Response()
    request_path = request.path
    print("\n----- Request Start ----->\n")
    print(request_path)
    print(request.headers)
    print("<----- Request End -----\n")
    response.headers['Content-Type'] = 'application/xml'
    data = ""
    with open('jsonincrement/json8823.jsonld', 'r') as file:
        data = file.read().replace('\n', '')
        file.close()
    response.text = data.encode(encoding='utf_8')
    return (response.text, response.status_code, response.headers.items())
    
@app.route('/increasing8500',methods = ['GET'])
def increasing8500():
    response = Response()
    request_path = request.path
    print("\n----- Request Start ----->\n")
    print(request_path)
    print(request.headers)
    print("<----- Request End -----\n")
    response.headers['Content-Type'] = 'application/xml'
    data = ""
    with open('jsonincrement/json9373.jsonld', 'r') as file:
        data = file.read().replace('\n', '')
        file.close()
    response.text = data.encode(encoding='utf_8')
    return (response.text, response.status_code, response.headers.items())
    
    
@app.route('/increasing9000',methods = ['GET'])
def increasing9000():
    response = Response()
    request_path = request.path
    print("\n----- Request Start ----->\n")
    print(request_path)
    print(request.headers)
    print("<----- Request End -----\n")
    response.headers['Content-Type'] = 'application/xml'
    data = ""
    with open('jsonincrement/json9924.jsonld', 'r') as file:
        data = file.read().replace('\n', '')
        file.close()
    response.text = data.encode(encoding='utf_8')
    return (response.text, response.status_code, response.headers.items())

if __name__ == '__main__':
   app.run(debug = True, threaded=True)