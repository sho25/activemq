begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Copyright 2009 Red Hat, Inc.  *  Red Hat licenses this file to you under the Apache License, version  *  2.0 (the "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *     http://www.apache.org/licenses/LICENSE-2.0  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or  *  implied.  See the License for the specific language governing  *  permissions and limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|hornetq
operator|.
name|javaee
operator|.
name|example
operator|.
name|server2
package|;
end_package

begin_comment
comment|/**  * A StatelessSenderService  *  * @author<a href="mailto:clebert.suconic@jboss.org">Clebert Suconic</a>  *  *  */
end_comment

begin_interface
specifier|public
interface|interface
name|StatelessSenderService
block|{
specifier|public
name|void
name|sendHello
parameter_list|(
name|String
name|message
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
end_interface

end_unit

