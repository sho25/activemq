begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|util
package|;
end_package

begin_class
specifier|public
class|class
name|JMXSupport
block|{
specifier|static
specifier|public
name|String
name|encodeObjectNamePart
parameter_list|(
name|String
name|part
parameter_list|)
block|{
name|String
name|answer
init|=
name|part
operator|.
name|replaceAll
argument_list|(
literal|"[\\:\\,\\'\\\"]"
argument_list|,
literal|"_"
argument_list|)
decl_stmt|;
name|answer
operator|=
name|answer
operator|.
name|replaceAll
argument_list|(
literal|"\\?"
argument_list|,
literal|"&qe;"
argument_list|)
expr_stmt|;
name|answer
operator|=
name|answer
operator|.
name|replaceAll
argument_list|(
literal|"="
argument_list|,
literal|"&amp;"
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
block|}
end_class

end_unit

