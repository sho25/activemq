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
name|command
operator|.
name|ActiveMQDestination
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
name|javax
operator|.
name|jms
operator|.
name|MessageListener
import|;
end_import

begin_comment
comment|/**  * Represents some kind of query which will load initial messages from some source for a new topic subscriber.  *   * @version $Revision$  */
end_comment

begin_interface
specifier|public
interface|interface
name|MessageQuery
block|{
comment|/**      * Executes the query for messages; each message is passed into the listener      *       * @param destination the destination on which the query is to be performed      * @param listener is the listener to notify as each message is created or loaded      */
specifier|public
name|void
name|execute
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|,
name|MessageListener
name|listener
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Returns true if the given update is valid and does not overlap with the initial message query.      * When performing an initial load from some source, there is a chance that an update may occur which is logically before      * the message sent on the initial load - so this method provides a hook where the query instance can keep track of the version IDs      * of the messages sent so that if an older version is sent as an update it can be excluded to avoid going backwards in time.      *       * e.g. if the execute() method creates version 2 of an object and then an update message is sent for version 1, this method should return false to       * hide the old update message.      *       * @param message the update message which may have been sent before the query actually completed      * @return true if the update message is valid otherwise false in which case the update message will be discarded.      */
specifier|public
name|boolean
name|validateUpdate
parameter_list|(
name|Message
name|message
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

