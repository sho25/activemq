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
name|jdbc
package|;
end_package

begin_comment
comment|/**  * Generates the SQL statements that are used by the JDBCAdapter.  *   * @version $Revision: 1.4 $  */
end_comment

begin_interface
specifier|public
interface|interface
name|StatementProvider
block|{
specifier|public
name|String
index|[]
name|getCreateSchemaStatments
parameter_list|()
function_decl|;
specifier|public
name|String
index|[]
name|getDropSchemaStatments
parameter_list|()
function_decl|;
specifier|public
name|String
name|getAddMessageStatment
parameter_list|()
function_decl|;
specifier|public
name|String
name|getUpdateMessageStatment
parameter_list|()
function_decl|;
specifier|public
name|String
name|getRemoveMessageStatment
parameter_list|()
function_decl|;
specifier|public
name|String
name|getFindMessageSequenceIdStatment
parameter_list|()
function_decl|;
specifier|public
name|String
name|getFindMessageStatment
parameter_list|()
function_decl|;
specifier|public
name|String
name|getFindAllMessagesStatment
parameter_list|()
function_decl|;
specifier|public
name|String
name|getFindLastSequenceIdInMsgs
parameter_list|()
function_decl|;
specifier|public
name|String
name|getFindLastSequenceIdInAcks
parameter_list|()
function_decl|;
specifier|public
name|String
name|getCreateDurableSubStatment
parameter_list|()
function_decl|;
specifier|public
name|String
name|getFindDurableSubStatment
parameter_list|()
function_decl|;
specifier|public
name|String
name|getUpdateLastAckOfDurableSub
parameter_list|()
function_decl|;
specifier|public
name|String
name|getFindAllDurableSubMessagesStatment
parameter_list|()
function_decl|;
specifier|public
name|String
name|getRemoveAllMessagesStatment
parameter_list|()
function_decl|;
specifier|public
name|String
name|getRemoveAllSubscriptionsStatment
parameter_list|()
function_decl|;
specifier|public
name|String
name|getDeleteSubscriptionStatment
parameter_list|()
function_decl|;
specifier|public
name|String
name|getDeleteOldMessagesStatment
parameter_list|()
function_decl|;
specifier|public
name|String
name|getFindAllDestinationsStatment
parameter_list|()
function_decl|;
specifier|public
name|void
name|setUseExternalMessageReferences
parameter_list|(
name|boolean
name|useExternalMessageReferences
parameter_list|)
function_decl|;
specifier|public
name|boolean
name|isUseExternalMessageReferences
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

