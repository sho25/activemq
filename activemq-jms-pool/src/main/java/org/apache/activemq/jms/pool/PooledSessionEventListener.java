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
name|jms
operator|.
name|pool
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TemporaryQueue
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TemporaryTopic
import|;
end_import

begin_interface
interface|interface
name|PooledSessionEventListener
block|{
comment|/**      * Called on successful creation of a new TemporaryQueue.      *      * @param tempQueue      *      The TemporaryQueue just created.      */
name|void
name|onTemporaryQueueCreate
parameter_list|(
name|TemporaryQueue
name|tempQueue
parameter_list|)
function_decl|;
comment|/**      * Called on successful creation of a new TemporaryTopic.      *      * @param tempTopic      *      The TemporaryTopic just created.      */
name|void
name|onTemporaryTopicCreate
parameter_list|(
name|TemporaryTopic
name|tempTopic
parameter_list|)
function_decl|;
comment|/**      * Called when the PooledSession is closed.      *      * @param session      *      The PooledSession that has been closed.      */
name|void
name|onSessionClosed
parameter_list|(
name|PooledSession
name|session
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

