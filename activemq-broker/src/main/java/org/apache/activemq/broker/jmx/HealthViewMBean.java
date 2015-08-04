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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularData
import|;
end_import

begin_comment
comment|/**  * Returns the status events of the broker to indicate any warnings.  */
end_comment

begin_interface
specifier|public
interface|interface
name|HealthViewMBean
block|{
specifier|public
name|TabularData
name|health
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**      * Warning this method can only be invoked if you have the correct version      * of {@link HealthStatus} on your classpath or you use something      * like<a href="http://jolokia.org/">jolokia</a> to access JMX.      *      * If in doubt, please use the {@link #getCurrentStatus()} method instead!      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"List of warnings and errors about the current health of the Broker - empty list is Good!"
argument_list|)
name|List
argument_list|<
name|HealthStatus
argument_list|>
name|healthList
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**      * @return String representation of the current Broker state      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"String representation of current Broker state"
argument_list|)
name|String
name|getCurrentStatus
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

