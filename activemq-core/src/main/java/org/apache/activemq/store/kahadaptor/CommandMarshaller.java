begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|store
operator|.
name|kahadaptor
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
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
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|command
operator|.
name|WireFormat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|packet
operator|.
name|ByteArrayPacket
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|packet
operator|.
name|Packet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|kaha
operator|.
name|Marshaller
import|;
end_import

begin_comment
comment|/**  * Marshall a Message or a MessageReference  * @version $Revision: 1.10 $  */
end_comment

begin_class
specifier|public
class|class
name|CommandMarshaller
implements|implements
name|Marshaller
block|{
specifier|private
name|WireFormat
name|wireFormat
decl_stmt|;
specifier|public
name|CommandMarshaller
parameter_list|(
name|WireFormat
name|wireFormat
parameter_list|)
block|{
name|this
operator|.
name|wireFormat
operator|=
name|wireFormat
expr_stmt|;
block|}
specifier|public
name|void
name|writePayload
parameter_list|(
name|Object
name|object
parameter_list|,
name|DataOutputStream
name|dataOut
parameter_list|)
throws|throws
name|IOException
block|{
name|Packet
name|packet
init|=
name|wireFormat
operator|.
name|marshal
argument_list|(
name|object
argument_list|)
decl_stmt|;
name|byte
index|[]
name|data
init|=
name|packet
operator|.
name|sliceAsBytes
argument_list|()
decl_stmt|;
name|dataOut
operator|.
name|writeInt
argument_list|(
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
name|dataOut
operator|.
name|write
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Object
name|readPayload
parameter_list|(
name|DataInputStream
name|dataIn
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|size
init|=
name|dataIn
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|size
index|]
decl_stmt|;
name|dataIn
operator|.
name|readFully
argument_list|(
name|data
argument_list|)
expr_stmt|;
return|return
name|wireFormat
operator|.
name|unmarshal
argument_list|(
operator|new
name|ByteArrayPacket
argument_list|(
name|data
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

