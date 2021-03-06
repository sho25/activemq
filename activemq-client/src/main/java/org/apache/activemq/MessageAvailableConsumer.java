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
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageConsumer
import|;
end_import

begin_comment
comment|/**  * An extended JMS interface that adds the ability to be notified when a   * message is available for consumption using the receive*() methods  * which is useful in Ajax style subscription models.  *   *   */
end_comment

begin_interface
specifier|public
interface|interface
name|MessageAvailableConsumer
extends|extends
name|MessageConsumer
block|{
comment|/**      * Sets the listener used to notify synchronous consumers that there is a message      * available so that the {@link MessageConsumer#receiveNoWait()} can be called.      */
name|void
name|setAvailableListener
parameter_list|(
name|MessageAvailableListener
name|availableListener
parameter_list|)
function_decl|;
comment|/**      * Gets the listener used to notify synchronous consumers that there is a message      * available so that the {@link MessageConsumer#receiveNoWait()} can be called.      */
name|MessageAvailableListener
name|getAvailableListener
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

