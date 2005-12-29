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
name|activeio
operator|.
name|xnet
package|;
end_package

begin_comment
comment|/**  *   */
end_comment

begin_class
specifier|public
class|class
name|ServiceException
extends|extends
name|Exception
block|{
comment|/**      *<p/>      * Default constructor, which simply delegates exception      * handling up the inheritance chain to<code>Exception</code>.      *</p>      */
specifier|public
name|ServiceException
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**      *<p/>      * This constructor allows a message to be supplied indicating the source      * of the problem that occurred.      *</p>      *      * @param message<code>String</code> identifying the cause of the problem.      */
specifier|public
name|ServiceException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
comment|/**      *<p/>      * This constructor allows a "root cause" exception to be supplied,      * which may later be used by the wrapping application.      *</p>      *      * @param rootCause<code>Throwable</code> that triggered the problem.      */
specifier|public
name|ServiceException
parameter_list|(
name|Throwable
name|rootCause
parameter_list|)
block|{
name|super
argument_list|(
name|rootCause
argument_list|)
expr_stmt|;
block|}
comment|/**      * This constructor allows both a message identifying the      * problem that occurred as well as a "root cause" exception      * to be supplied, which may later be used by the wrapping      * application.      *      * @param message<code>String</code> identifying the cause of the problem.      * @param rootCause<code>Throwable</code> that triggered this problem.      */
specifier|public
name|ServiceException
parameter_list|(
name|String
name|message
parameter_list|,
name|Throwable
name|rootCause
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|,
name|rootCause
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

