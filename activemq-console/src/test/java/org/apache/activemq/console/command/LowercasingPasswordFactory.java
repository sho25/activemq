begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|console
operator|.
name|command
package|;
end_package

begin_class
specifier|public
class|class
name|LowercasingPasswordFactory
implements|implements
name|PasswordFactory
block|{
annotation|@
name|Override
specifier|public
name|String
name|getPassword
parameter_list|(
name|String
name|password
parameter_list|)
block|{
return|return
name|password
operator|.
name|toLowerCase
argument_list|()
return|;
block|}
block|}
end_class

begin_empty_stmt
empty_stmt|;
end_empty_stmt

end_unit

