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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|MessageId
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
name|StoreEntry
import|;
end_import

begin_comment
comment|/**  * Holds information for location of message  *   *   */
end_comment

begin_class
specifier|public
class|class
name|ConsumerMessageRef
block|{
specifier|private
name|MessageId
name|messageId
decl_stmt|;
specifier|private
name|StoreEntry
name|messageEntry
decl_stmt|;
specifier|private
name|StoreEntry
name|ackEntry
decl_stmt|;
comment|/**      * @return the ackEntry      */
specifier|public
name|StoreEntry
name|getAckEntry
parameter_list|()
block|{
return|return
name|this
operator|.
name|ackEntry
return|;
block|}
comment|/**      * @param ackEntry the ackEntry to set      */
specifier|public
name|void
name|setAckEntry
parameter_list|(
name|StoreEntry
name|ackEntry
parameter_list|)
block|{
name|this
operator|.
name|ackEntry
operator|=
name|ackEntry
expr_stmt|;
block|}
comment|/**      * @return the messageEntry      */
specifier|public
name|StoreEntry
name|getMessageEntry
parameter_list|()
block|{
return|return
name|this
operator|.
name|messageEntry
return|;
block|}
comment|/**      * @param messageEntry the messageEntry to set      */
specifier|public
name|void
name|setMessageEntry
parameter_list|(
name|StoreEntry
name|messageEntry
parameter_list|)
block|{
name|this
operator|.
name|messageEntry
operator|=
name|messageEntry
expr_stmt|;
block|}
comment|/**      * @return the messageId      */
specifier|public
name|MessageId
name|getMessageId
parameter_list|()
block|{
return|return
name|this
operator|.
name|messageId
return|;
block|}
comment|/**      * @param messageId the messageId to set      */
specifier|public
name|void
name|setMessageId
parameter_list|(
name|MessageId
name|messageId
parameter_list|)
block|{
name|this
operator|.
name|messageId
operator|=
name|messageId
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"ConsumerMessageRef["
operator|+
name|messageId
operator|+
literal|"]"
return|;
block|}
block|}
end_class

end_unit

