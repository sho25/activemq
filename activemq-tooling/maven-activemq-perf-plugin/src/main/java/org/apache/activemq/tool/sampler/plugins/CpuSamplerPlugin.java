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
name|tool
operator|.
name|sampler
operator|.
name|plugins
package|;
end_package

begin_interface
specifier|public
interface|interface
name|CpuSamplerPlugin
block|{
name|String
name|WINDOWS_2000
init|=
literal|"Windows 2000"
decl_stmt|;
name|String
name|WINDOWS_NT
init|=
literal|"Windows NT"
decl_stmt|;
name|String
name|WINDOWS_XP
init|=
literal|"Windows XP"
decl_stmt|;
name|String
name|WINDOWS_95
init|=
literal|"Windows 95"
decl_stmt|;
name|String
name|WINDOWS_CE
init|=
literal|"Windows CE"
decl_stmt|;
name|String
name|LINUX
init|=
literal|"Linux"
decl_stmt|;
name|String
name|SOLARIS
init|=
literal|"Solaris"
decl_stmt|;
name|String
name|AIX
init|=
literal|"AIX"
decl_stmt|;
name|String
name|FREEBSD
init|=
literal|"FreeBSD"
decl_stmt|;
name|String
name|MAC_OS
init|=
literal|"Mac OS"
decl_stmt|;
name|String
name|MAC_OS_X
init|=
literal|"Mac OS X"
decl_stmt|;
name|String
name|POWERPC
init|=
literal|"PowerPC"
decl_stmt|;
name|String
name|OS_2
init|=
literal|"OS/2"
decl_stmt|;
name|String
name|getCpuUtilizationStats
parameter_list|()
function_decl|;
name|void
name|start
parameter_list|()
function_decl|;
name|void
name|stop
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

