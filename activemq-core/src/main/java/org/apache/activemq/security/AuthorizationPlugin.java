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
name|security
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
name|Broker
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
name|BrokerPlugin
import|;
end_import

begin_comment
comment|/**  * An authorization plugin where each operation on a destination is checked  * against an authorizationMap  *   * @org.apache.xbean.XBean  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|AuthorizationPlugin
implements|implements
name|BrokerPlugin
block|{
specifier|private
name|AuthorizationMap
name|map
decl_stmt|;
specifier|public
name|AuthorizationPlugin
parameter_list|()
block|{     }
specifier|public
name|AuthorizationPlugin
parameter_list|(
name|AuthorizationMap
name|map
parameter_list|)
block|{
name|this
operator|.
name|map
operator|=
name|map
expr_stmt|;
block|}
specifier|public
name|Broker
name|installPlugin
parameter_list|(
name|Broker
name|broker
parameter_list|)
block|{
if|if
condition|(
name|map
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"You must configure a 'map' property"
argument_list|)
throw|;
block|}
return|return
operator|new
name|AuthorizationBroker
argument_list|(
name|broker
argument_list|,
name|map
argument_list|)
return|;
block|}
specifier|public
name|AuthorizationMap
name|getMap
parameter_list|()
block|{
return|return
name|map
return|;
block|}
specifier|public
name|void
name|setMap
parameter_list|(
name|AuthorizationMap
name|map
parameter_list|)
block|{
name|this
operator|.
name|map
operator|=
name|map
expr_stmt|;
block|}
block|}
end_class

end_unit

