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
name|command
operator|.
name|CommandTypes
import|;
end_import

begin_comment
comment|/**  * Base class for  messages/acknowledgements for a transaction  *   * @version $Revision: 1.4 $  */
end_comment

begin_class
class|class
name|TxCommand
block|{
specifier|protected
name|Object
name|messageStoreKey
decl_stmt|;
specifier|protected
name|BaseCommand
name|command
decl_stmt|;
comment|/**          * @return Returns the messageStoreKey.          */
specifier|public
name|Object
name|getMessageStoreKey
parameter_list|()
block|{
return|return
name|messageStoreKey
return|;
block|}
comment|/**          * @param messageStoreKey The messageStoreKey to set.          */
specifier|public
name|void
name|setMessageStoreKey
parameter_list|(
name|Object
name|messageStoreKey
parameter_list|)
block|{
name|this
operator|.
name|messageStoreKey
operator|=
name|messageStoreKey
expr_stmt|;
block|}
comment|/**          * @return Returns the command.          */
specifier|public
name|BaseCommand
name|getCommand
parameter_list|()
block|{
return|return
name|command
return|;
block|}
comment|/**          * @param command The command to set.          */
specifier|public
name|void
name|setCommand
parameter_list|(
name|BaseCommand
name|command
parameter_list|)
block|{
name|this
operator|.
name|command
operator|=
name|command
expr_stmt|;
block|}
comment|/**          * @return true if a Message command          */
specifier|public
name|boolean
name|isAdd
parameter_list|()
block|{
return|return
name|command
operator|!=
literal|null
operator|&&
name|command
operator|.
name|getDataStructureType
argument_list|()
operator|!=
name|CommandTypes
operator|.
name|MESSAGE_ACK
return|;
block|}
comment|/**          * @return true if a MessageAck command          */
specifier|public
name|boolean
name|isRemove
parameter_list|()
block|{
return|return
name|command
operator|!=
literal|null
operator|&&
name|command
operator|.
name|getDataStructureType
argument_list|()
operator|==
name|CommandTypes
operator|.
name|MESSAGE_ACK
return|;
block|}
block|}
end_class

end_unit

