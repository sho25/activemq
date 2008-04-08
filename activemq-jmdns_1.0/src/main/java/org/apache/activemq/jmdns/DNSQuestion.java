begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|//Copyright 2003-2005 Arthur van Hoff, Rick Blair
end_comment

begin_comment
comment|//Licensed under Apache License version 2.0
end_comment

begin_comment
comment|//Original license LGPL
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|jmdns
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Logger
import|;
end_import

begin_comment
comment|/**  * A DNS question.  *  * @version %I%, %G%  * @author	Arthur van Hoff  */
end_comment

begin_class
specifier|final
class|class
name|DNSQuestion
extends|extends
name|DNSEntry
block|{
specifier|private
specifier|static
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|DNSQuestion
operator|.
name|class
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
comment|/**      * Create a question.      */
name|DNSQuestion
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|type
parameter_list|,
name|int
name|clazz
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|type
argument_list|,
name|clazz
argument_list|)
expr_stmt|;
block|}
comment|/**      * Check if this question is answered by a given DNS record.      */
name|boolean
name|answeredBy
parameter_list|(
name|DNSRecord
name|rec
parameter_list|)
block|{
return|return
operator|(
name|clazz
operator|==
name|rec
operator|.
name|clazz
operator|)
operator|&&
operator|(
operator|(
name|type
operator|==
name|rec
operator|.
name|type
operator|)
operator|||
operator|(
name|type
operator|==
name|DNSConstants
operator|.
name|TYPE_ANY
operator|)
operator|)
operator|&&
name|name
operator|.
name|equals
argument_list|(
name|rec
operator|.
name|name
argument_list|)
return|;
block|}
comment|/**      * For debugging only.      */
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|toString
argument_list|(
literal|"question"
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
end_class

end_unit

