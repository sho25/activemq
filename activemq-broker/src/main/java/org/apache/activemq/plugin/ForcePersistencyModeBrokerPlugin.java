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
name|plugin
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

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * A Plugin which allows to force every incoming message to be PERSISTENT or NON-PERSISTENT.   *   * Useful, if you have set the broker usage policy to process ONLY persistent or ONLY non-persistent  * messages.   *  @org.apache.xbean.XBean element="forcePersistencyModeBrokerPlugin"  */
end_comment

begin_class
specifier|public
class|class
name|ForcePersistencyModeBrokerPlugin
implements|implements
name|BrokerPlugin
block|{
specifier|private
specifier|static
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ForcePersistencyModeBrokerPlugin
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|boolean
name|persistenceFlag
init|=
literal|false
decl_stmt|;
comment|/**  * Constructor  */
specifier|public
name|ForcePersistencyModeBrokerPlugin
parameter_list|()
block|{   }
comment|/**   * @param broker  * @return the Broker  * @throws Exception  * @see org.apache.activemq.broker.BrokerPlugin#installPlugin(org.apache.activemq.broker.Broker)  */
specifier|public
name|Broker
name|installPlugin
parameter_list|(
name|Broker
name|broker
parameter_list|)
throws|throws
name|Exception
block|{
name|ForcePersistencyModeBroker
name|pB
init|=
operator|new
name|ForcePersistencyModeBroker
argument_list|(
name|broker
argument_list|)
decl_stmt|;
name|pB
operator|.
name|setPersistenceFlag
argument_list|(
name|isPersistenceForced
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Installing ForcePersistencyModeBroker plugin: persistency enforced={}"
argument_list|,
name|pB
operator|.
name|isPersistent
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|pB
return|;
block|}
comment|/** Sets the persistency mode.    *      * @param persistenceFlag    */
specifier|public
name|void
name|setPersistenceFlag
parameter_list|(
specifier|final
name|boolean
name|persistenceFlag
parameter_list|)
block|{
name|this
operator|.
name|persistenceFlag
operator|=
name|persistenceFlag
expr_stmt|;
block|}
comment|/**    * @return the mode the (activated) plugin will set the message delivery mode     */
specifier|public
specifier|final
name|boolean
name|isPersistenceForced
parameter_list|()
block|{
return|return
name|persistenceFlag
return|;
block|}
block|}
end_class

end_unit

