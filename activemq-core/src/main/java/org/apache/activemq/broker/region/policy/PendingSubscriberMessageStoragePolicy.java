begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE  * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file  * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the  * License. You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the  * specific language governing permissions and limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|region
operator|.
name|policy
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
name|broker
operator|.
name|region
operator|.
name|cursors
operator|.
name|PendingMessageCursor
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
name|Store
import|;
end_import

begin_comment
comment|/** * Abstraction to allow different policies for holding messages awaiting dispatch to active clients *  * @version $Revision$ */
end_comment

begin_interface
specifier|public
interface|interface
name|PendingSubscriberMessageStoragePolicy
block|{
comment|/**      * Retrieve the configured pending message storage cursor;      *       * @param name      * @param tmpStorage      * @param maxBatchSize      * @return the Pending Message cursor      */
specifier|public
name|PendingMessageCursor
name|getSubscriberPendingMessageCursor
parameter_list|(
name|String
name|name
parameter_list|,
name|Store
name|tmpStorage
parameter_list|,
name|int
name|maxBatchSize
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

