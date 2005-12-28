begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|ra
package|;
end_package

begin_comment
comment|/**  * Thrown to indicate that a MessageEndpoint is no longer valid  * and should be discarded.   *    * @author<a href="mailto:michael.gaffney@panacya.com">Michael Gaffney</a>  */
end_comment

begin_class
specifier|public
class|class
name|InvalidMessageEndpointException
extends|extends
name|RuntimeException
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|9007051892399939057L
decl_stmt|;
comment|/**      * Constructs a new exception with<code>null</code> as its detail message.      * The cause is not initialized, and may subsequently be initialized by a      * call to {@link #initCause}.      */
specifier|public
name|InvalidMessageEndpointException
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**      * Constructs a new exception with the specified detail message.  The      * cause is not initialized, and may subsequently be initialized by      * a call to {@link #initCause}.      *      * @param message the detail message. The detail message is saved for      *                later retrieval by the {@link #getMessage()} method.      */
specifier|public
name|InvalidMessageEndpointException
parameter_list|(
specifier|final
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
comment|/**      * Constructs a new exception with the specified detail message and      * cause.<p>Note that the detail message associated with      *<code>cause</code> is<i>not</i> automatically incorporated in      * this exception's detail message.      *      * @param message the detail message (which is saved for later retrieval      *                by the {@link #getMessage()} method).      * @param cause   the cause (which is saved for later retrieval by the      *                {@link #getCause()} method).  (A<tt>null</tt> value is      *                permitted, and indicates that the cause is nonexistent or      *                unknown.)      */
specifier|public
name|InvalidMessageEndpointException
parameter_list|(
specifier|final
name|String
name|message
parameter_list|,
specifier|final
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs a new exception with the specified cause and a detail      * message of<tt>(cause==null ? null : cause.toString())</tt> (which      * typically contains the class and detail message of<tt>cause</tt>).      * This constructor is useful for exceptions that are little more than      * wrappers for other throwables (for example, {@link      * java.security.PrivilegedActionException}).      *      * @param cause the cause (which is saved for later retrieval by the      *              {@link #getCause()} method).  (A<tt>null</tt> value is      *              permitted, and indicates that the cause is nonexistent or      *              unknown.)      */
specifier|public
name|InvalidMessageEndpointException
parameter_list|(
specifier|final
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|cause
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

