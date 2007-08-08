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
name|amq
package|;
end_package

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
name|Iterator
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
name|JournalTopicAck
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
name|Message
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
name|MessageAck
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
name|async
operator|.
name|Location
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_comment
comment|/**  * Operations  * @version $Revision: 1.6 $  */
end_comment

begin_class
specifier|public
class|class
name|AMQTx
block|{
specifier|private
specifier|final
name|Location
name|location
decl_stmt|;
specifier|private
name|ArrayList
argument_list|<
name|AMQTxOperation
argument_list|>
name|operations
init|=
operator|new
name|ArrayList
argument_list|<
name|AMQTxOperation
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|AMQTx
parameter_list|(
name|Location
name|location
parameter_list|)
block|{
name|this
operator|.
name|location
operator|=
name|location
expr_stmt|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|AMQMessageStore
name|store
parameter_list|,
name|Message
name|msg
parameter_list|,
name|Location
name|location
parameter_list|)
block|{
name|operations
operator|.
name|add
argument_list|(
operator|new
name|AMQTxOperation
argument_list|(
name|AMQTxOperation
operator|.
name|ADD_OPERATION_TYPE
argument_list|,
name|store
operator|.
name|getDestination
argument_list|()
argument_list|,
name|msg
argument_list|,
name|location
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|AMQMessageStore
name|store
parameter_list|,
name|MessageAck
name|ack
parameter_list|)
block|{
name|operations
operator|.
name|add
argument_list|(
operator|new
name|AMQTxOperation
argument_list|(
name|AMQTxOperation
operator|.
name|REMOVE_OPERATION_TYPE
argument_list|,
name|store
operator|.
name|getDestination
argument_list|()
argument_list|,
name|ack
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|AMQTopicMessageStore
name|store
parameter_list|,
name|JournalTopicAck
name|ack
parameter_list|)
block|{
name|operations
operator|.
name|add
argument_list|(
operator|new
name|AMQTxOperation
argument_list|(
name|AMQTxOperation
operator|.
name|ACK_OPERATION_TYPE
argument_list|,
name|store
operator|.
name|getDestination
argument_list|()
argument_list|,
name|ack
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Message
index|[]
name|getMessages
parameter_list|()
block|{
name|ArrayList
argument_list|<
name|Object
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|AMQTxOperation
argument_list|>
name|iter
init|=
name|operations
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|AMQTxOperation
name|op
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|op
operator|.
name|getOperationType
argument_list|()
operator|==
name|AMQTxOperation
operator|.
name|ADD_OPERATION_TYPE
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|op
operator|.
name|getData
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|Message
name|rc
index|[]
init|=
operator|new
name|Message
index|[
name|list
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|list
operator|.
name|toArray
argument_list|(
name|rc
argument_list|)
expr_stmt|;
return|return
name|rc
return|;
block|}
specifier|public
name|MessageAck
index|[]
name|getAcks
parameter_list|()
block|{
name|ArrayList
argument_list|<
name|Object
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|AMQTxOperation
argument_list|>
name|iter
init|=
name|operations
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|AMQTxOperation
name|op
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|op
operator|.
name|getOperationType
argument_list|()
operator|==
name|AMQTxOperation
operator|.
name|REMOVE_OPERATION_TYPE
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|op
operator|.
name|getData
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|MessageAck
name|rc
index|[]
init|=
operator|new
name|MessageAck
index|[
name|list
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|list
operator|.
name|toArray
argument_list|(
name|rc
argument_list|)
expr_stmt|;
return|return
name|rc
return|;
block|}
comment|/**      * @return the location      */
specifier|public
name|Location
name|getLocation
parameter_list|()
block|{
return|return
name|this
operator|.
name|location
return|;
block|}
specifier|public
name|ArrayList
argument_list|<
name|AMQTxOperation
argument_list|>
name|getOperations
parameter_list|()
block|{
return|return
name|operations
return|;
block|}
specifier|public
name|void
name|setOperations
parameter_list|(
name|ArrayList
argument_list|<
name|AMQTxOperation
argument_list|>
name|operations
parameter_list|)
block|{
name|this
operator|.
name|operations
operator|=
name|operations
expr_stmt|;
block|}
block|}
end_class

end_unit

