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
name|util
package|;
end_package

begin_comment
comment|/**  * A simple callback object used by the  * {@link org.apache.activemq.util.TransactionTemplate}  * and {@link org.apache.activemq.util.ExceptionTemplate}    objects to provide automatic transactional or exception handling blocks.  *  * @version $Revision: 1.2 $  */
end_comment

begin_interface
specifier|public
interface|interface
name|Callback
block|{
comment|/**      * Executes some piece of code within a transaction      * performing a commit if there is no exception thrown      * else a rollback is performed      *      * @throws Throwable      */
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|Throwable
function_decl|;
block|}
end_interface

end_unit

