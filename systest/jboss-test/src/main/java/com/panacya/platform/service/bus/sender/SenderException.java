begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|com
operator|.
name|panacya
operator|.
name|platform
operator|.
name|service
operator|.
name|bus
operator|.
name|sender
package|;
end_package

begin_comment
comment|/**  * @author<a href="mailto:michael.gaffney@panacya.com">Michael Gaffney</a>  */
end_comment

begin_class
specifier|public
class|class
name|SenderException
extends|extends
name|Exception
block|{
comment|/**      * Constructs a new exception with<code>null</code> as its detail message.      * The cause is not initialized, and may subsequently be initialized by a      * call to {@link #initCause}.      */
specifier|public
name|SenderException
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**      * Constructs a new exception with the specified detail message.  The      * cause is not initialized, and may subsequently be initialized by      * a call to {@link #initCause}.      *      * @param message the detail message. The detail message is saved for      *                later retrieval by the {@link #getMessage()} method.      */
specifier|public
name|SenderException
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
name|SenderException
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
name|SenderException
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

