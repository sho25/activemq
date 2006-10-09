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
name|kaha
operator|.
name|impl
operator|.
name|index
operator|.
name|IndexItem
import|;
end_import

begin_comment
comment|/**  * Marshall a TopicSubAck  * @version $Revision: 1.10 $  */
end_comment

begin_class
specifier|public
class|class
name|ConsumerMessageRefMarshaller
implements|implements
name|Marshaller
block|{
comment|/**      * @param object      * @param dataOut      * @throws IOException      * @see org.apache.activemq.kaha.Marshaller#writePayload(java.lang.Object, java.io.DataOutput)      */
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
name|ConsumerMessageRef
name|ref
init|=
operator|(
name|ConsumerMessageRef
operator|)
name|object
decl_stmt|;
name|IndexItem
name|item
init|=
operator|(
name|IndexItem
operator|)
name|ref
operator|.
name|getMessageEntry
argument_list|()
decl_stmt|;
name|dataOut
operator|.
name|writeLong
argument_list|(
name|item
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
name|item
operator|.
name|write
argument_list|(
name|dataOut
argument_list|)
expr_stmt|;
name|item
operator|=
operator|(
name|IndexItem
operator|)
name|ref
operator|.
name|getAckEntry
argument_list|()
expr_stmt|;
name|dataOut
operator|.
name|writeLong
argument_list|(
name|item
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
name|item
operator|.
name|write
argument_list|(
name|dataOut
argument_list|)
expr_stmt|;
block|}
comment|/**      * @param dataIn      * @return payload      * @throws IOException      * @see org.apache.activemq.kaha.Marshaller#readPayload(java.io.DataInput)      */
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
name|ConsumerMessageRef
name|ref
init|=
operator|new
name|ConsumerMessageRef
argument_list|()
decl_stmt|;
name|IndexItem
name|item
init|=
operator|new
name|IndexItem
argument_list|()
decl_stmt|;
name|item
operator|.
name|setOffset
argument_list|(
name|dataIn
operator|.
name|readLong
argument_list|()
argument_list|)
expr_stmt|;
name|item
operator|.
name|read
argument_list|(
name|dataIn
argument_list|)
expr_stmt|;
name|ref
operator|.
name|setMessageEntry
argument_list|(
name|item
argument_list|)
expr_stmt|;
name|item
operator|=
operator|new
name|IndexItem
argument_list|()
expr_stmt|;
name|item
operator|.
name|setOffset
argument_list|(
name|dataIn
operator|.
name|readLong
argument_list|()
argument_list|)
expr_stmt|;
name|item
operator|.
name|read
argument_list|(
name|dataIn
argument_list|)
expr_stmt|;
name|ref
operator|.
name|setAckEntry
argument_list|(
name|item
argument_list|)
expr_stmt|;
return|return
name|ref
return|;
block|}
block|}
end_class

end_unit

