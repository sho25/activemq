begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|transport
operator|.
name|tcp
package|;
end_package

begin_comment
comment|/**  * Thrown to indicate that the {@link TcpTransportServer#maximumConnections}   * property has been exceeded.   *   * @see {@link TcpTransportServer#maximumConnections}  * @author bsnyder  *  */
end_comment

begin_class
specifier|public
class|class
name|ExceededMaximumConnectionsException
extends|extends
name|Exception
block|{
comment|/**      * Default serial version id for serialization      */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|1166885550766355524L
decl_stmt|;
specifier|public
name|ExceededMaximumConnectionsException
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
block|}
end_class

end_unit

