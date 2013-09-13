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
name|broker
package|;
end_package

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
comment|/**  * A useful base class for implementing broker plugins.  *   *   */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|BrokerPluginSupport
extends|extends
name|MutableBrokerFilter
implements|implements
name|BrokerPlugin
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|BrokerPluginSupport
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|BrokerPluginSupport
parameter_list|()
block|{
name|super
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
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
name|setNext
argument_list|(
name|broker
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|start
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Broker Plugin {} started"
argument_list|,
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|stop
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Broker Plugin {} stopped"
argument_list|,
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

