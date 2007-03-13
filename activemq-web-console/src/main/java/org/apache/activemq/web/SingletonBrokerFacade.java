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
name|web
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
name|BrokerService
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
name|broker
operator|.
name|BrokerRegistry
import|;
end_import

begin_comment
comment|/**  * A facade for the broker in the same JVM and ClassLoader  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|SingletonBrokerFacade
extends|extends
name|LocalBrokerFacade
block|{
specifier|public
name|SingletonBrokerFacade
parameter_list|()
block|{
name|super
argument_list|(
name|findSingletonBroker
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
specifier|static
name|BrokerService
name|findSingletonBroker
parameter_list|()
block|{
name|BrokerService
name|broker
init|=
name|BrokerRegistry
operator|.
name|getInstance
argument_list|()
operator|.
name|findFirst
argument_list|()
decl_stmt|;
if|if
condition|(
name|broker
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No BrokerService is registered with the BrokerRegistry."
operator|+
literal|" Are you sure there is a configured broker in the same ClassLoader?"
argument_list|)
throw|;
block|}
return|return
name|broker
return|;
block|}
block|}
end_class

end_unit

