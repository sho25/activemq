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
operator|.
name|jmx
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
name|Service
import|;
end_import

begin_interface
specifier|public
interface|interface
name|ConnectorViewMBean
extends|extends
name|Service
block|{
annotation|@
name|MBeanInfo
argument_list|(
literal|"Connection count"
argument_list|)
name|int
name|connectionCount
parameter_list|()
function_decl|;
comment|/**      * Resets the statistics      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Resets the statistics"
argument_list|)
name|void
name|resetStatistics
parameter_list|()
function_decl|;
comment|/**      * enable statistics gathering      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Enables statistics gathering"
argument_list|)
name|void
name|enableStatistics
parameter_list|()
function_decl|;
comment|/**      * disable statistics gathering      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Disables statistics gathering"
argument_list|)
name|void
name|disableStatistics
parameter_list|()
function_decl|;
comment|/**      * Returns true if statistics is enabled      *       * @return true if statistics is enabled      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Statistics gathering enabled"
argument_list|)
name|boolean
name|isStatisticsEnabled
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

