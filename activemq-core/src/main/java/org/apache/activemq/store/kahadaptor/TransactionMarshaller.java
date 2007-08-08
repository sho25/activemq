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
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|command
operator|.
name|BaseCommand
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
name|wireformat
operator|.
name|WireFormat
import|;
end_import

begin_comment
comment|/**  * Marshall a Transaction  * @version $Revision: 1.10 $  */
end_comment

begin_class
specifier|public
class|class
name|TransactionMarshaller
implements|implements
name|Marshaller
block|{
specifier|private
name|WireFormat
name|wireFormat
decl_stmt|;
specifier|public
name|TransactionMarshaller
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
name|DataOutput
name|dataOut
parameter_list|)
throws|throws
name|IOException
block|{
name|KahaTransaction
name|kt
init|=
operator|(
name|KahaTransaction
operator|)
name|object
decl_stmt|;
name|List
name|list
init|=
name|kt
operator|.
name|getList
argument_list|()
decl_stmt|;
name|dataOut
operator|.
name|writeInt
argument_list|(
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|list
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|TxCommand
name|tx
init|=
operator|(
name|TxCommand
operator|)
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Object
name|key
init|=
name|tx
operator|.
name|getMessageStoreKey
argument_list|()
decl_stmt|;
name|ByteSequence
name|packet
init|=
name|wireFormat
operator|.
name|marshal
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|dataOut
operator|.
name|writeInt
argument_list|(
name|packet
operator|.
name|length
argument_list|)
expr_stmt|;
name|dataOut
operator|.
name|write
argument_list|(
name|packet
operator|.
name|data
argument_list|,
name|packet
operator|.
name|offset
argument_list|,
name|packet
operator|.
name|length
argument_list|)
expr_stmt|;
name|Object
name|command
init|=
name|tx
operator|.
name|getCommand
argument_list|()
decl_stmt|;
name|packet
operator|=
name|wireFormat
operator|.
name|marshal
argument_list|(
name|command
argument_list|)
expr_stmt|;
name|dataOut
operator|.
name|writeInt
argument_list|(
name|packet
operator|.
name|length
argument_list|)
expr_stmt|;
name|dataOut
operator|.
name|write
argument_list|(
name|packet
operator|.
name|data
argument_list|,
name|packet
operator|.
name|offset
argument_list|,
name|packet
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
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
name|KahaTransaction
name|result
init|=
operator|new
name|KahaTransaction
argument_list|()
decl_stmt|;
name|List
name|list
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|result
operator|.
name|setList
argument_list|(
name|list
argument_list|)
expr_stmt|;
name|int
name|number
init|=
name|dataIn
operator|.
name|readInt
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|number
condition|;
name|i
operator|++
control|)
block|{
name|TxCommand
name|command
init|=
operator|new
name|TxCommand
argument_list|()
decl_stmt|;
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
name|Object
name|key
init|=
name|wireFormat
operator|.
name|unmarshal
argument_list|(
operator|new
name|ByteSequence
argument_list|(
name|data
argument_list|)
argument_list|)
decl_stmt|;
name|command
operator|.
name|setMessageStoreKey
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|size
operator|=
name|dataIn
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|data
operator|=
operator|new
name|byte
index|[
name|size
index|]
expr_stmt|;
name|dataIn
operator|.
name|readFully
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|BaseCommand
name|bc
init|=
operator|(
name|BaseCommand
operator|)
name|wireFormat
operator|.
name|unmarshal
argument_list|(
operator|new
name|ByteSequence
argument_list|(
name|data
argument_list|)
argument_list|)
decl_stmt|;
name|command
operator|.
name|setCommand
argument_list|(
name|bc
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

