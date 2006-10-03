begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|kaha
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_comment
comment|/**  * Implementation of a Marshaller for Objects  *   * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|public
class|class
name|ObjectMarshaller
implements|implements
name|Marshaller
block|{
comment|/**      * Write the payload of this entry to the RawContainer      *       * @param object      * @param dataOut      * @throws IOException      */
specifier|public
name|void
name|writePayload
parameter_list|(
name|Object
name|object
parameter_list|,
name|DataOutput
name|dataOut
parameter_list|)
throws|throws
name|IOException
block|{
comment|// I failed to see why we just did not just used the provided stream directly
comment|//        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
comment|//        ObjectOutputStream objectOut=new ObjectOutputStream(bytesOut);
comment|//        objectOut.writeObject(object);
comment|//        objectOut.close();
comment|//        byte[] data = bytesOut.toByteArray();
comment|//        dataOut.writeInt(data.length);
comment|//        dataOut.write(data);
name|ObjectOutputStream
name|objectOut
init|=
operator|new
name|ObjectOutputStream
argument_list|(
operator|(
name|OutputStream
operator|)
name|dataOut
argument_list|)
decl_stmt|;
name|objectOut
operator|.
name|writeObject
argument_list|(
name|object
argument_list|)
expr_stmt|;
name|objectOut
operator|.
name|flush
argument_list|()
expr_stmt|;
name|objectOut
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
comment|/**      * Read the entry from the RawContainer      *       * @param dataIn      * @return unmarshalled object      * @throws IOException      */
specifier|public
name|Object
name|readPayload
parameter_list|(
name|DataInput
name|dataIn
parameter_list|)
throws|throws
name|IOException
block|{
comment|// I failed to see why we just did not just used the provided stream directly
comment|//        int size = dataIn.readInt();
comment|//        byte[] data = new byte[size];
comment|//        dataIn.readFully(data);
comment|//        ByteArrayInputStream bytesIn = new ByteArrayInputStream(data);
comment|//        ObjectInputStream objectIn=new ObjectInputStream(bytesIn);
comment|//        try{
comment|//            return objectIn.readObject();
comment|//        }catch(ClassNotFoundException e){
comment|//            throw new IOException(e.getMessage());
comment|//        }
name|ObjectInputStream
name|objectIn
init|=
operator|new
name|ObjectInputStream
argument_list|(
operator|(
name|InputStream
operator|)
name|dataIn
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|objectIn
operator|.
name|readObject
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

