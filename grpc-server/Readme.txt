grpcurl.exe --plaintext localhost:9090 list
grpcurl.exe --plaintext localhost:9090 list com.example.grpc.server.grpcserver.HelloService

grpcurl.exe --plaintext -d "{\"firstName\":\"raju\", \"lastName\": \"How are you?\"}" localhost:9090 com.example.grpc.server.grpcserver.HelloService/hello


\"a00\" : \"1\",
\"a01\" : \"2\",
\"a10\" : \"3\",
\"a11\" : \"4\",
\"b00\" : \"5\",
\"b01\" : \"6\",
\"b10\" : \"7\",
\"b11\" : \"8\",


grpcurl.exe --plaintext -d "{\"a00\" : \"1\",\"a01\" : \"2\",\"a10\" : \"3\",\"a11\" : \"4\",\"b00\" : \"5\",\"b01\" : \"6\",\"b10\" : \"7\",\"b11\" : \"8\"}" localhost:9090 matrixmult.MatrixService/MultiplyBlock