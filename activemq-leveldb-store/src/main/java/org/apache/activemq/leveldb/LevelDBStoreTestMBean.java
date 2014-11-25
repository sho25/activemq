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
name|leveldb
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
name|jmx
operator|.
name|MBeanInfo
import|;
end_import

begin_comment
comment|/**  *<p>  *</p>  *  * @author<a href="http://hiramchirino.com">Hiram Chirino</a>  */
end_comment

begin_interface
specifier|public
interface|interface
name|LevelDBStoreTestMBean
block|{
annotation|@
name|MBeanInfo
argument_list|(
literal|"Used to set if the log force calls should be suspended"
argument_list|)
name|void
name|setSuspendForce
parameter_list|(
name|boolean
name|value
parameter_list|)
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Gets if the log force calls should be suspended"
argument_list|)
name|boolean
name|getSuspendForce
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Gets the number of threads waiting to do a log force call."
argument_list|)
name|long
name|getForceCalls
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Used to set if the log write calls should be suspended"
argument_list|)
name|void
name|setSuspendWrite
parameter_list|(
name|boolean
name|value
parameter_list|)
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Gets if the log write calls should be suspended"
argument_list|)
name|boolean
name|getSuspendWrite
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Gets the number of threads waiting to do a log write call."
argument_list|)
name|long
name|getWriteCalls
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Used to set if the log delete calls should be suspended"
argument_list|)
name|void
name|setSuspendDelete
parameter_list|(
name|boolean
name|value
parameter_list|)
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Gets if the log delete calls should be suspended"
argument_list|)
name|boolean
name|getSuspendDelete
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Gets the number of threads waiting to do a log delete call."
argument_list|)
name|long
name|getDeleteCalls
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

