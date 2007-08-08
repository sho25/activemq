begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|wireformat
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
name|DataInputStream
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|ByteArrayInputStream
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
name|util
operator|.
name|ByteArrayOutputStream
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
name|util
operator|.
name|ByteSequence
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
name|util
operator|.
name|ClassLoadingAwareObjectInputStream
import|;
end_import

begin_comment
comment|/**  * A simple implementation which uses Object Stream serialization.  *   * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|ObjectStreamWireFormat
implements|implements
name|WireFormat
block|{
specifier|public
name|ByteSequence
name|marshal
parameter_list|(
name|Object
name|command
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|DataOutputStream
name|ds
init|=
operator|new
name|DataOutputStream
argument_list|(
name|baos
argument_list|)
decl_stmt|;
name|marshal
argument_list|(
name|command
argument_list|,
name|ds
argument_list|)
expr_stmt|;
name|ds
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|baos
operator|.
name|toByteSequence
argument_list|()
return|;
block|}
specifier|public
name|Object
name|unmarshal
parameter_list|(
name|ByteSequence
name|packet
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|unmarshal
argument_list|(
operator|new
name|DataInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|packet
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|void
name|marshal
parameter_list|(
name|Object
name|command
parameter_list|,
name|DataOutput
name|ds
parameter_list|)
throws|throws
name|IOException
block|{
name|ObjectOutputStream
name|out
init|=
operator|new
name|ObjectOutputStream
argument_list|(
operator|(
name|OutputStream
operator|)
name|ds
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeObject
argument_list|(
name|command
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|out
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
specifier|public
name|Object
name|unmarshal
parameter_list|(
name|DataInput
name|ds
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|ClassLoadingAwareObjectInputStream
name|in
init|=
operator|new
name|ClassLoadingAwareObjectInputStream
argument_list|(
operator|(
name|InputStream
operator|)
name|ds
argument_list|)
decl_stmt|;
name|Object
name|command
decl_stmt|;
name|command
operator|=
name|in
operator|.
name|readObject
argument_list|()
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|command
return|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|(
name|IOException
operator|)
operator|new
name|IOException
argument_list|(
literal|"unmarshal failed: "
operator|+
name|e
argument_list|)
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|setVersion
parameter_list|(
name|int
name|version
parameter_list|)
block|{     }
specifier|public
name|int
name|getVersion
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
end_class

end_unit

