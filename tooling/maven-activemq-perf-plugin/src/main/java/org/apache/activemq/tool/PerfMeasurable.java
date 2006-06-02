begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_interface
specifier|public
interface|interface
name|PerfMeasurable
block|{
specifier|public
name|void
name|reset
parameter_list|()
function_decl|;
specifier|public
name|long
name|getThroughput
parameter_list|()
function_decl|;
specifier|public
name|Map
name|getClientSettings
parameter_list|()
function_decl|;
specifier|public
name|void
name|setPerfEventListener
parameter_list|(
name|PerfEventListener
name|listener
parameter_list|)
function_decl|;
specifier|public
name|PerfEventListener
name|getPerfEventListener
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

